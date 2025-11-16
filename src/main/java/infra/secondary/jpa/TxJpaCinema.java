package infra.secondary.jpa;

import hexagon.Cinema;
import hexagon.primary.port.*;
import hexagon.secondary.port.ForGeneratingTokens;
import hexagon.secondary.port.ForManagingPayments;
import hexagon.secondary.port.ForSendingEmailNotifications;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.RollbackException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class TxJpaCinema implements CinemaSystem {

    private static final int NUMBER_OF_RETRIES = 2;

    private final EntityManagerFactory emf;
    private final ForManagingPayments forPayments;
    private final ForSendingEmailNotifications forSendingEmails;
    private final ForGeneratingTokens token;
    private final DateTimeProvider timeProvider;
    private int pageSize = 10;

    public TxJpaCinema(EntityManagerFactory emf,
                       ForManagingPayments forPayments,
                       ForSendingEmailNotifications forSendingEmails, ForGeneratingTokens forGeneratingTokens,
                       DateTimeProvider timeProvider) {
        this.emf = emf;
        this.forPayments = forPayments;
        this.forSendingEmails = forSendingEmails;
        this.token = forGeneratingTokens;
        this.timeProvider = timeProvider;
    }

    public TxJpaCinema(EntityManagerFactory emf,
                       ForManagingPayments forPayments,
                       ForSendingEmailNotifications forSendingEmails, ForGeneratingTokens forGeneratingTokens,
                       DateTimeProvider timeProvider, int pageSize) {
        this(emf, forPayments, forSendingEmails, forGeneratingTokens, timeProvider);
        this.pageSize = pageSize;
    }

    @Override
    public List<MovieShows> showsUntil(LocalDateTime untilTo) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.showsUntil(untilTo);
        });
    }

    @Override
    public List<MovieInfo> pagedMoviesSortedByName(int pageNumber) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.pagedMoviesSortedByName(pageNumber);
        });
    }

    @Override
    public List<MovieInfo> pagedMoviesSortedByRate(int pageNumber) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.pagedMoviesSortedByRate(pageNumber);
        });
    }

    @Override
    public List<MovieInfo> pagedMoviesSortedByReleaseDate(int pageNumber) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.pagedMoviesSortedByReleaseDate(pageNumber);
        });
    }

    @Override
    public MovieInfo movie(String id) {
        return emf.callInTransaction(em -> {
            // Or have Cinema already instantiated and use a setter assigning the entityManager
            // or instantiate Cinema every time and use a propper constructor. For me this is better.
            // Always create ready to use instances, better for understanding the use of the class.
            var cinema = createCinema(em);
            return cinema.movie(id);
        });
    }

    @Override
    public DetailedShowInfo show(String id) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.show(id);
        });
    }

    @Override
    public MovieInfo addNewMovie(String name, int duration,
                                 LocalDate releaseDate, String plot, Set<Genre> genres) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.addNewMovie(name, duration, releaseDate, plot,
                    genres);
        });
    }

    private Cinema createCinema(EntityManager em) {
        return new Cinema(new JpaForManagingMovies(em, this.pageSize),
                new JpaForManagingShows(em),
                new JpaForManagingUsers(em),
                this.forPayments, this.forSendingEmails,
                this.timeProvider, this.token);
    }

    @Override
    public MovieInfo addActorTo(String movieId, String name, String surname,
                                String email, String characterName) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.addActorTo(movieId, name, surname,
                    email, characterName);
        });
    }

    @Override
    public MovieInfo addDirectorTo(String movieId, String name,
                                   String surname, String email) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.addDirectorTo(movieId, name, surname,
                    email);
        });
    }

    @Override
    public String addNewTheater(String name, Set<Integer> seatsNumbers) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.addNewTheater(name, seatsNumbers);
        });
    }

    @Override
    public ShowInfo addNewShowFor(String movieId, LocalDateTime startTime,
                                  float price, String theaterId, int pointsToWin) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.addNewShowFor(movieId, startTime, price, theaterId,
                    pointsToWin);
        });
    }

    @Override
    public DetailedShowInfo reserve(String userId, String showTimeId,
                                    Set<Integer> selectedSeats) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.reserve(userId, showTimeId, selectedSeats);
        });
    }

    @Override
    public Ticket pay(String userId, String showTimeId,
                      Set<Integer> selectedSeats,
                      String creditCardNumber, YearMonth expirationDate,
                      String secturityCode) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.pay(userId, showTimeId, selectedSeats, creditCardNumber, expirationDate, secturityCode);
        });
    }

    @Override
    public UserMovieRate rateMovieBy(String userId, String idMovie,
                                     int rateValue,
                                     String comment) {
        return inTxWithRetriesOnConflict(em -> {
            var cinema = createCinema(em);
            return cinema.rateMovieBy(userId, idMovie, rateValue, comment);
        });
    }

    @Override
    public List<UserMovieRate> pagedRatesOfOrderedDate(String movieId,
                                                       int pageNumber) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.pagedRatesOfOrderedDate(movieId, pageNumber);
        });
    }

    @Override
    public List<MovieInfo> pagedSearchMovieByName(String fullOrPartmovieName,
                                                  int pageNumber) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.pagedSearchMovieByName(fullOrPartmovieName,
                    pageNumber);
        });
    }

    @Override
    public String login(String username, String password) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.login(username, password);
        });
    }

    @Override
    public String userIdFrom(String token) {
        return emf.callInTransaction(em -> createCinema(em).userIdFrom(token));
    }

    @Override
    public void changePassword(String userId, String currentPassword,
                               String newPassword1, String newPassword2) {
        emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            cinema.changePassword(userId, currentPassword, newPassword1,
                    newPassword2);
            // just to conform the compiler
            return null;
        });
    }

    @Override
    public UserProfile profileFrom(String userId) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return cinema.profileFrom(userId);
        });
    }

    @Override
    public String registerUser(String name, String surname, String email,
                               String userName, String password, String repeatPassword) {
        return inTxWithRetriesOnConflict((em) -> {
            var cinema = createCinema(em);
            return cinema.registerUser(name, surname, email, userName, password,
                    repeatPassword);
        });
    }
    
    private <T> T inTxWithRetriesOnConflict(
            Function<EntityManager, T> toExecute) {
        int retries = 0;
        while (retries < NUMBER_OF_RETRIES) {
            try {
                return emf.callInTransaction(toExecute);
                // There is no a great way in JPA to detect a constraint
                // violation. I use RollbackException and retries one more
                // time for specific use cases
            } catch (RollbackException e) {
                // jakarta.persistence.RollbackException
                retries++;
            }
        }
        throw new BusinessException(
                "Trasaction could not be completed due to concurrency conflic");
    }
}
