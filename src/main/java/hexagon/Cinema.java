package hexagon;

import hexagon.primary.port.*;
import hexagon.secondary.port.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

public class Cinema implements CinemaSystem {
    static final int MINUTES_TO_KEEP_RESERVATION = 5;
    static final String USER_NAME_ALREADY_EXISTS = "userName already exists";
    static final String MOVIE_ID_DOES_NOT_EXISTS = "Movie ID not found";
    //TODO: ver que hacemos con estas constantes que no se usan... donde esta esta logica?
    static final String SHOW_TIME_ID_NOT_EXISTS = "Show ID not found";
    static final String USER_ID_NOT_EXISTS = "User not registered";
    static final String CREDIT_CARD_DEBIT_HAS_FAILED = "Credit card debit have failed";
    static final String USER_HAS_ALREADY_RATE = "The user has already rate the movie";
    static final String PAGE_NUMBER_MUST_BE_GREATER_THAN_ZERO = "page number must be greater than zero";
    public static final String USER_OR_PASSWORD_ERROR = "Invalid username or password";

    private ForManagingCreditCardPayments paymentGateway;
    private ForSendingEmailNotifications emailProvider;
    private EntityManager em;
    private DateTimeProvider dateTimeProvider;
    private Token token;
    private ForManagingMovies forManagingMovies;
    private ForManagingShows forManagingShows;
    private ForManagingUsers forManagingUsers;

    public Cinema(ForManagingMovies managingMovies,
                  ForManagingShows managingShows,
                  ForManagingUsers managingUsers,
                  ForManagingCreditCardPayments paymentGateway,
                  ForSendingEmailNotifications emailProvider,
                  DateTimeProvider provider,
                  Token token) {
        this.forManagingMovies = managingMovies;
        this.forManagingShows = managingShows;
        this.forManagingUsers = managingUsers;
        this.paymentGateway = paymentGateway;
        this.emailProvider = emailProvider;
        this.token = token;
        this.dateTimeProvider = provider;
    }

    //TODO: borrar
    public Cinema(ForManagingCreditCardPayments paymentGateway,
                  ForSendingEmailNotifications emailProvider,
                  DateTimeProvider provider,
                  Token token) {
        this.paymentGateway = paymentGateway;
        this.emailProvider = emailProvider;
        this.token = token;
        this.dateTimeProvider = provider;
    }

    @Override
    public List<MovieShows> showsUntil(LocalDateTime untilTo) {
        return movieShowsUntil(untilTo);
    }

    private List<MovieShows> movieShowsUntil(LocalDateTime untilTo) {
        var movies = forManagingMovies.moviesWithShowsUntil(untilTo);
        return movies.stream()
                .map(Movie::toMovieShow)
                .toList();
    }

    // DONE
    @Override
    public MovieInfo movie(String id) {
        try {
            return movieWithActorsById(id);
        } catch (NonUniqueResultException | NoResultException e) {
            throw new BusinessException(MOVIE_ID_DOES_NOT_EXISTS);
        }
    }

    private MovieInfo movieWithActorsById(String id) {
        var movie = this.forManagingMovies.movieBy(id);
        return movie.toInfo();
    }

    // DONE
    @Override
    public MovieInfo addNewMovie(String name, int duration,
                                 LocalDate releaseDate, String plot, Set<Genre> genres) {
        var movie = new Movie(name, plot, duration, releaseDate, genres);
        this.forManagingMovies.newMovie(movie);
        return movie.toInfo();
    }

    // DONE
    @Override
    public MovieInfo addActorTo(String movieId, String name, String surname,
                                String email, String characterName) {
        var movie = forManagingMovies.movieBy(movieId);
        var actor = movie.addAnActor(name, surname, email, characterName);
        forManagingMovies.addAnActor(movieId, actor.id(), actor.name(),
                actor.surname(),
                actor.email(), actor.characterName());
        return movie.toInfo();
    }

    // DONE
    @Override
    public MovieInfo addDirectorTo(String movieId, String name,
                                   String surname, String email) {
        var movie = forManagingMovies.movieBy(movieId);
        var director = movie.addADirector(name, surname, email);
        forManagingMovies.addDirector(movieId, director.id(),
                director.name(),
                director.surname(), director.email());
        return movie.toInfo();
    }

    // DONE
    @Override
    public String addNewTheater(String name, Set<Integer> seatsNumbers) {
        var theater = new Theater(name, seatsNumbers);
        forManagingShows.addTheater(theater);
        return theater.id();
    }

    @Override
    public ShowInfo addNewShowFor(String movieId, LocalDateTime startTime,
                                  float price, String theaterId, int pointsToWin) {
        var movie = forManagingMovies.movieBy(movieId);
        var theatre = forManagingShows.theaterBy(theaterId);
        var showTime = new ShowTime(movie, startTime, price, theatre,
                pointsToWin);
        forManagingShows.newShow(showTime);
        return showTime.toShowInfo();
    }

    @Override
    //TODO: probar luego que concurrentement funcione el locking optimista
    public DetailedShowInfo reserve(String userId, String showTimeId,
                                    Set<Integer> selectedSeats) {
        var showTime = forManagingShows.showTimeBy(showTimeId);
        var user = forManagingUsers.userById(userId);
        var selectedShowSeats = showTime.reserveSeatsFor(user, selectedSeats,
                this.dateTimeProvider.now().plusMinutes(MINUTES_TO_KEEP_RESERVATION));
        forManagingShows.reserve(selectedShowSeats);
        return showTime.toDetailedInfo();
    }

    @Override
    public Ticket pay(String userId, String showTimeId,
                      Set<Integer> selectedSeats,
                      String creditCardNumber, YearMonth expirationDate,
                      String secturityCode) {
        ShowTime showTime = forManagingShows.showTimeBy(showTimeId);
        var user = forManagingUsers.userById(userId);
        var showSeats = showTime.confirmSeatsForUser(user, selectedSeats);
        var totalAmount = showTime.totalAmountForTheseSeats(selectedSeats);
        tryCreditCardDebit(creditCardNumber, expirationDate, secturityCode,
                totalAmount);
        sendNewSaleEmailToTheUser(selectedSeats, showTime, user,
                totalAmount);
        var sale = new Sale(totalAmount, user, showTime,
                showTime.pointsToEarn(), selectedSeats);
        forManagingShows.confirm(sale, showSeats);
        return sale.ticket();
    }

    @Override
    public String login(String username, String password) {
        var mightBeAUser = forManagingUsers.userBy(username, password);
        var user = mightBeAUser
                .orElseThrow(() -> new AuthException(USER_OR_PASSWORD_ERROR));
        forManagingUsers.auditSuccessLogin(user.id(),
                this.dateTimeProvider.now());
        return token.tokenFrom(user.toMap());
    }

    @Override
    public String registerUser(String name, String surname, String email,
                               String userName,
                               String password, String repeatPassword) {
        checkUserNameAlreadyExists(userName);
        var user = new User(new Person(name, surname, email), userName,
                password,
                repeatPassword);
        // The User object does not have getters for password to prevent
        // mistakes
        forManagingUsers.register(user, password);
        return user.id();
    }

    @Override
    public UserMovieRate rateMovieBy(String userId, String movieId,
                                     int rateValue,
                                     String comment) {
        checkUserIsRatingSameMovieTwice(userId, movieId);
        var user = forManagingUsers.userById(userId);
        var movie = forManagingMovies.movieBy(movieId);
        var userRate = movie.rateBy(user, rateValue, comment);
        forManagingMovies.updateRating(movie);
        return userRate.toUserMovieRate();
    }

    private void checkUserIsRatingSameMovieTwice(String userId,
                                                 String movieId) {
        boolean alreadyRate = forManagingMovies.doesThisUserRateTheMovie(userId,
                movieId);
        if (alreadyRate) {
            throw new BusinessException(USER_HAS_ALREADY_RATE);
        }
    }

    private void checkUserNameAlreadyExists(String userName) {
        if (forManagingUsers.existsUserBy(userName)) {
            throw new BusinessException(USER_NAME_ALREADY_EXISTS);
        }
    }

    private void tryCreditCardDebit(String creditCardNumber,
                                    YearMonth expirationDate, String secturityCode, float totalAmount) {
        try {
            this.paymentGateway.pay(creditCardNumber, expirationDate,
                    secturityCode, totalAmount);
        } catch (Exception e) {
            throw new BusinessException(CREDIT_CARD_DEBIT_HAS_FAILED, e);
        }
    }

    private void sendNewSaleEmailToTheUser(Set<Integer> selectedSeats,
                                           ShowTime showTime, User user, float totalAmount) {
        var emailTemplate = new NewSaleEmailTemplate(totalAmount,
                user.getUserName(), selectedSeats, showTime.movieName(),
                showTime.startDateTime());

        this.emailProvider.send(user.email(), emailTemplate.subject(),
                emailTemplate.body());
    }

    @Override
    public List<UserMovieRate> pagedRatesOfOrderedDate(String movieId,
                                                       int pageNumber) {
        checkPageNumberIsGreaterThanZero(pageNumber);
        var userRates = forManagingMovies.pagedRatesOrderedByDate(movieId,
                pageNumber);
        return userRates.stream()
                .map(rate -> rate.toUserMovieRate()).toList();
    }

    @Override
    public DetailedShowInfo show(String id) {
        var show = forManagingShows.showTimeBy(id);
        return show.toDetailedInfo();
    }

    @Override
    public List<MovieInfo> pagedSearchMovieByName(String fullOrPartmovieName,
                                                  int pageNumber) {
        checkPageNumberIsGreaterThanZero(pageNumber);
        var movies = forManagingMovies
                .pagedSearchMovieByName(fullOrPartmovieName, pageNumber);
        return moviesToMovieInfo(movies);
    }

    private void checkPageNumberIsGreaterThanZero(int pageNumber) {
        if (pageNumber <= 0) {
            throw new BusinessException(PAGE_NUMBER_MUST_BE_GREATER_THAN_ZERO);
        }
    }

    // DONE
    @Override
    public List<MovieInfo> pagedMoviesSortedByName(int pageNumber) {
        checkPageNumberIsGreaterThanZero(pageNumber);
        var movies = forManagingMovies.pagedMoviesSortedByName(pageNumber);
        return moviesToMovieInfo(movies);
    }

    // DONE
    @Override
    public List<MovieInfo> pagedMoviesSortedByReleaseDate(int pageNumber) {
        checkPageNumberIsGreaterThanZero(pageNumber);
        var movies = forManagingMovies
                .pagedMoviesSortedByReleaseDate(pageNumber);
        return moviesToMovieInfo(movies);
    }

    // DONE
    @Override
    public List<MovieInfo> pagedMoviesSortedByRate(int pageNumber) {
        checkPageNumberIsGreaterThanZero(pageNumber);
        var movies = forManagingMovies.pagedMoviesSortedByRate(pageNumber);
        return moviesToMovieInfo(movies);
    }

    private List<MovieInfo> moviesToMovieInfo(List<Movie> movies) {
        return movies.stream().map(m -> m.toInfo()).toList();
    }

    // DONE
    @Override
    public String userIdFrom(String token) {
        return this.token.verifyAndGetUserIdFrom(token);
    }

    // DONE
    @Override
    public UserProfile profileFrom(String userId) {
        return forManagingUsers.userById(userId).toProfile();
    }

    // DONE
    @Override
    public void changePassword(String userId, String currentPassword,
                               String newPassword1, String newPassword2) {
        var user = forManagingUsers.userById(userId);
        user.changePassword(currentPassword, newPassword1, newPassword2);
        forManagingUsers.changePassword(user.id(), newPassword1);
    }
}