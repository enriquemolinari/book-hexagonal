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
import java.util.function.Consumer;
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
        return callInTx(cinema -> cinema.showsUntil(untilTo));
    }

    @Override
    public List<MovieInfo> pagedMoviesSortedByName(int pageNumber) {
        return callInTx(cinema -> cinema.pagedMoviesSortedByName(pageNumber));
    }

    @Override
    public List<MovieInfo> pagedMoviesSortedByRate(int pageNumber) {
        return callInTx(cinema -> cinema.pagedMoviesSortedByRate(pageNumber));
    }

    @Override
    public List<MovieInfo> pagedMoviesSortedByReleaseDate(int pageNumber) {
        return callInTx(cinema -> cinema.pagedMoviesSortedByReleaseDate(pageNumber));
    }

    @Override
    public MovieInfo movie(String id) {
        return callInTx(cinema -> cinema.movie(id));
    }

    @Override
    public DetailedShowInfo show(String id) {
        return callInTx(cinema -> cinema.show(id));
    }

    @Override
    public MovieInfo addNewMovie(String name, int duration,
                                 LocalDate releaseDate, String plot, Set<Genre> genres) {
        return callInTx(cinema -> cinema.addNewMovie(name, duration, releaseDate, plot, genres));
    }

    @Override
    public MovieInfo addActorTo(String movieId, String name, String surname,
                                String email, String characterName) {
        return callInTx(cinema -> cinema.addActorTo(movieId, name, surname, email, characterName));
    }

    @Override
    public MovieInfo addDirectorTo(String movieId, String name,
                                   String surname, String email) {
        return callInTx(cinema -> cinema.addDirectorTo(movieId, name, surname, email));
    }

    @Override
    public String addNewTheater(String name, Set<Integer> seatsNumbers) {
        return callInTx(cinema -> cinema.addNewTheater(name, seatsNumbers));
    }

    @Override
    public ShowInfo addNewShowFor(String movieId, LocalDateTime startTime,
                                  float price, String theaterId, int pointsToWin) {
        return callInTx(cinema -> cinema.addNewShowFor(movieId, startTime, price, theaterId, pointsToWin));
    }

    @Override
    public DetailedShowInfo reserve(String userId, String showTimeId,
                                    Set<Integer> selectedSeats) {
        return callInTx(cinema -> cinema.reserve(userId, showTimeId, selectedSeats));
    }

    @Override
    public Ticket pay(String userId, String showTimeId,
                      Set<Integer> selectedSeats,
                      String creditCardNumber, YearMonth expirationDate,
                      String secturityCode) {
        return callInTx(cinema -> cinema.pay(userId, showTimeId, selectedSeats, creditCardNumber, expirationDate, secturityCode));
    }

    @Override
    public List<UserMovieRate> pagedRatesOfOrderedDate(String movieId,
                                                       int pageNumber) {
        return callInTx(cinema -> cinema.pagedRatesOfOrderedDate(movieId, pageNumber));
    }

    @Override
    public List<MovieInfo> pagedSearchMovieByName(String fullOrPartmovieName,
                                                  int pageNumber) {
        return callInTx(cinema -> cinema.pagedSearchMovieByName(fullOrPartmovieName, pageNumber));
    }

    @Override
    public String login(String username, String password) {
        return callInTx(cinema -> cinema.login(username, password));
    }

    @Override
    public String userIdFrom(String token) {
        return callInTx(cinema -> cinema.userIdFrom(token));
    }

    @Override
    public void changePassword(String userId, String currentPassword,
                               String newPassword1, String newPassword2) {
        execInTx(cinema -> cinema.changePassword(userId, currentPassword, newPassword1, newPassword2));
    }

    @Override
    public UserProfile profileFrom(String userId) {
        return callInTx(cinema -> cinema.profileFrom(userId));
    }

    @Override
    public String registerUser(String name, String surname, String email,
                               String userName, String password, String repeatPassword) {
        return inTxWithRetriesOnConflict(cinema -> {
            return cinema.registerUser(name, surname, email, userName, password,
                    repeatPassword);
        });
    }

    @Override
    public UserMovieRate rateMovieBy(String userId, String idMovie,
                                     int rateValue,
                                     String comment) {
        return inTxWithRetriesOnConflict(cinema -> {
            return cinema.rateMovieBy(userId, idMovie, rateValue, comment);
        });
    }

    private Cinema createCinema(EntityManager em) {
        return new Cinema(new JpaForManagingMovies(em, this.pageSize),
                new JpaForManagingShows(em),
                new JpaForManagingUsers(em),
                this.forPayments, this.forSendingEmails,
                this.timeProvider, this.token);
    }

    public void execInTx(Consumer<CinemaSystem> toExecute) {
        emf.runInTransaction(em -> {
            var cinema = createCinema(em);
            toExecute.accept(cinema);
        });
    }

    public <T> T callInTx(Function<CinemaSystem, T> toExecuteAndReturn) {
        return emf.callInTransaction(em -> {
            var cinema = createCinema(em);
            return toExecuteAndReturn.apply(cinema);
        });
    }

    private <T> T inTxWithRetriesOnConflict(
            Function<CinemaSystem, T> toExecute) {
        int retries = 0;
        while (retries < NUMBER_OF_RETRIES) {
            try {
                return emf.callInTransaction(em -> {
                    var cinema = createCinema(em);
                    return toExecute.apply(cinema);
                });
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
