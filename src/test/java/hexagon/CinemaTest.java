package hexagon;

import hexagon.primary.port.*;
import infra.secondary.inmemory.HashMapForManagingMovies;
import infra.secondary.inmemory.HashMapForManagingUsers;
import infra.secondary.inmemory.HashMapForManangingShows;
import infra.secondary.jpa.TxJpaCinema;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

import static hexagon.ForTests.*;
import static org.junit.jupiter.api.Assertions.*;

public class CinemaTest {

    private static final String JOSEUSER_SURNAME = "aSurname";
    private static final String JOSEUSER_NAME = "Jose";
    private static final String JOSEUSER_PASS = "password12345679";
    private static final String JOSEUSER_EMAIL = "jose@bla.com";
    private static final YearMonth JOSEUSER_CREDIT_CARD_EXPIRITY = YearMonth.of(
            LocalDateTime.now().getYear(),
            LocalDateTime.now().plusMonths(2).getMonth());
    private static final String JOSEUSER_CREDIT_CARD_SEC_CODE = "145";
    private static final String JOSEUSER_CREDIT_CARD_NUMBER = "123-456-789";
    private static final String JOSEUSER_USERNAME = "joseuser";
    private static final String ANTONIOUSER_USERNAME = "antonio";
    private final ForTests tests = new ForTests();

    private static EntityManagerFactory emf;

    @BeforeEach
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("test-derby-cinema");
    }

    @ParameterizedTest
    @MethodSource(value = "createCinema")
    public void aShowIsPlayingAt(CinemaSystem cinema) {
        var movieInfo = tests.createSuperMovie(cinema);
        String theaterId = createATheater(cinema);
        cinema.addNewShowFor(movieInfo.id(),
                LocalDateTime.of(LocalDate.now().plusYears(1).getYear(), 10, 10,
                        13, 30),
                10f, theaterId, 20);
        cinema.addNewShowFor(movieInfo.id(),
                LocalDateTime.of(LocalDate.now().plusYears(2).getYear(),
                        5, 10,
                        13, 30),
                10f, theaterId, 20);
        var movieShows = cinema
                .showsUntil(
                        LocalDateTime.of(LocalDate.now().plusYears(1).getYear(),
                                10, 10, 13, 31));
        assertEquals(1, movieShows.size());
        assertEquals("1hr 49mins", movieShows.get(0).duration());
        assertEquals(1, movieShows.get(0).shows().size());
        assertEquals(10f, movieShows.get(0).shows().get(0).price());
        assertEquals(SUPER_MOVIE_NAME, movieShows.get(0).movieName());
    }

    private static List<CinemaSystem> createCinemaPageSize2() {
        return createCinema(2, DateTimeProvider.create());
    }

    private static List<CinemaSystem> createCinemaPageSize1() {
        return createCinema(1, DateTimeProvider.create());
    }

    private static List<CinemaSystem> createCinema() {
        return createCinema(10, DateTimeProvider.create());
    }

    private static List<CinemaSystem> createCinemaWithDateTimeProvider() {
        return createCinema(10, () -> LocalDateTime.now().minusMonths(1));
    }

    private static List<CinemaSystem> createCinema(int pageSize, DateTimeProvider provider) {
        var tests = new ForTests();
        var emf = Persistence.createEntityManagerFactory("test-derby-cinema");
        return List.of(new TxJpaCinema(emf, tests.doNothingPaymentProvider(),
                        tests.doNothingEmailProvider(), tests.doNothingToken(), provider, pageSize),
                new Cinema(new HashMapForManagingMovies(pageSize), new HashMapForManangingShows(),
                        new HashMapForManagingUsers(),
                        tests.doNothingPaymentProvider(), tests.doNothingEmailProvider(),
                        provider, tests.doNothingToken()));
    }

    @ParameterizedTest
    @MethodSource("createCinemaWithDateTimeProvider")
    public void iCanReserveAnExpiredReservation(CinemaSystem cinema) {
        var movieInfo = tests.createSuperMovie(cinema);
        String theaterId = createATheater(cinema);
        var showInfo = cinema.addNewShowFor(movieInfo.id(),
                LocalDateTime.of(LocalDate.now().plusYears(1).getYear(), 10, 10,
                        13, 30),
                10f, theaterId, 20);
        var joseUserId = registerUserJose(cinema);
        var userId = registerAUser(cinema);
        cinema.reserve(joseUserId, showInfo.showId(), Set.of(1, 5));
        // if exception is not thrown it means I was able to make the reservation
        var info = cinema.reserve(userId, showInfo.showId(), Set.of(1, 5));
        // in any case all is available because I have reserved with a date provider in the past
        assertTrue(info.currentSeats().contains(new Seat(1, true)));
        assertTrue(info.currentSeats().contains(new Seat(2, true)));
        assertTrue(info.currentSeats().contains(new Seat(3, true)));
        assertTrue(info.currentSeats().contains(new Seat(4, true)));
        assertTrue(info.currentSeats().contains(new Seat(5, true)));
    }

    @ParameterizedTest
    @MethodSource(value = "createCinema")
    public void changePasswordOk(CinemaSystem cinema) {
        var userId = registerUserJose(cinema);
        cinema.changePassword(userId, JOSEUSER_PASS, "1234567Passw",
                "1234567Passw");
        assertNotNull(cinema.login(JOSEUSER_USERNAME, "1234567Passw"));
    }

    @ParameterizedTest
    @MethodSource("createCinema")
    public void reserveSeats(CinemaSystem cinema) {
        var movieInfo = tests.createSuperMovie(cinema);
        String theaterId = createATheater(cinema);
        var showInfo = cinema.addNewShowFor(movieInfo.id(),
                LocalDateTime.of(LocalDate.now().plusYears(1).getYear(), 10, 10,
                        13, 30),
                10f, theaterId, 20);
        var userId = registerAUser(cinema);
        var info = cinema.reserve(userId, showInfo.showId(), Set.of(1, 5));
        assertTrue(info.currentSeats().contains(new Seat(1, false)));
        assertTrue(info.currentSeats().contains(new Seat(2, true)));
        assertTrue(info.currentSeats().contains(new Seat(3, true)));
        assertTrue(info.currentSeats().contains(new Seat(4, true)));
        assertTrue(info.currentSeats().contains(new Seat(5, false)));
    }

    @ParameterizedTest
    @MethodSource("createCinema")
    public void retrieveShow(CinemaSystem cinema) {
        var movieInfo = tests.createSuperMovie(cinema);
        var theaterId = createATheater(cinema);
        var showInfo = cinema.addNewShowFor(movieInfo.id(),
                LocalDateTime.of(LocalDate.now().plusYears(1).getYear(), 10, 10,
                        13, 30),
                10f, theaterId, 20);
        var userId = registerAUser(cinema);
        cinema.reserve(userId, showInfo.showId(), Set.of(1, 5));
        var info = cinema.show(showInfo.showId());
        assertEquals(SUPER_MOVIE_NAME, info.info().movieName());
        assertEquals("1hr 49mins", info.info().movieDuration());
        assertTrue(info.currentSeats().contains(new Seat(1, false)));
        assertTrue(info.currentSeats().contains(new Seat(2, true)));
        assertTrue(info.currentSeats().contains(new Seat(3, true)));
        assertTrue(info.currentSeats().contains(new Seat(4, true)));
        assertTrue(info.currentSeats().contains(new Seat(5, false)));
    }

    @ParameterizedTest
    @MethodSource("createCinema")
    public void reserveAlreadReservedSeats(CinemaSystem cinema) {
        var movieInfo = tests.createSuperMovie(cinema);
        var theaterId = createATheater(cinema);
        var showInfo = cinema.addNewShowFor(movieInfo.id(),
                LocalDateTime.of(LocalDate.now().plusYears(1).getYear(), 10, 10,
                        13, 30),
                10f, theaterId, 20);
        var userId = registerAUser(cinema);
        var joseId = registerUserJose(cinema);
        cinema.reserve(userId, showInfo.showId(), Set.of(1, 5));
        var e = assertThrows(BusinessException.class, () -> {
            cinema.reserve(joseId, showInfo.showId(), Set.of(1, 4, 3));
            fail("I have reserved an already reserved seat");
        });
        assertEquals(ShowTime.SELECTED_SEATS_ARE_BUSY, e.getMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "createCinema")
    public void loginOk(CinemaSystem cinema) {
        registerUserJose(cinema);
        var token = cinema.login(JOSEUSER_USERNAME, JOSEUSER_PASS);
        assertEquals("aToken", token);
    }

    @ParameterizedTest
    @MethodSource(value = "createCinema")
    public void loginFail(CinemaSystem cinema) {
        registerUserJose(cinema);
        var e = assertThrows(AuthException.class, () -> {
            cinema.login(JOSEUSER_USERNAME, "wrongPassword");
            fail("A user has logged in with a wrong password");
        });
        assertEquals(Cinema.USER_OR_PASSWORD_ERROR, e.getMessage());
    }

    @ParameterizedTest
    @MethodSource(value = "createCinema")
    public void registerAUserNameTwice(CinemaSystem cinema) {
        registerUserJose(cinema);
        var e = assertThrows(BusinessException.class, () -> {
            registerUserJose(cinema);
            fail("I have registered the same userName twice");
        });
        assertEquals(Cinema.USER_NAME_ALREADY_EXISTS, e.getMessage());
    }

    @Test
    public void jpaConfirmAndPaySeats() {
        var fakePaymenentProvider = tests.fakePaymenentProvider();
        var fakeEmailProvider = tests.fakeEmailProvider();
        var cinema = new TxJpaCinema(emf, fakePaymenentProvider, fakeEmailProvider,
                tests.doNothingToken(), DateTimeProvider.create(), 10);
        confirmAndPaySeats(cinema, fakePaymenentProvider, fakeEmailProvider);
    }

    @Test
    public void hashMapConfirmAndPaySeats() {
        var fakePaymenentProvider = tests.fakePaymenentProvider();
        var fakeEmailProvider = tests.fakeEmailProvider();
        var cinema = new Cinema(new HashMapForManagingMovies(10), new HashMapForManangingShows(),
                new HashMapForManagingUsers(),
                fakePaymenentProvider, fakeEmailProvider,
                DateTimeProvider.create(), tests.doNothingToken());
        confirmAndPaySeats(cinema, fakePaymenentProvider, fakeEmailProvider);
    }

    private void confirmAndPaySeats(CinemaSystem cinema,
                                    PaymenentProviderFake fakePaymenentProvider,
                                    EmailProviderFake fakeEmailProvider) {
        var movieInfo = tests.createSuperMovie(cinema);
        var theaterId = createATheater(cinema);
        var showInfo = cinema.addNewShowFor(movieInfo.id(),
                LocalDateTime.of(LocalDate.now().plusYears(1).getYear(), 10, 10,
                        13, 30),
                10f, theaterId, 20);
        var joseId = registerUserJose(cinema);
        cinema.reserve(joseId, showInfo.showId(), Set.of(1, 5));
        var ticket = cinema.pay(joseId, showInfo.showId(), Set.of(1, 5),
                JOSEUSER_CREDIT_CARD_NUMBER,
                JOSEUSER_CREDIT_CARD_EXPIRITY,
                JOSEUSER_CREDIT_CARD_SEC_CODE);
        assertTrue(ticket.hasSeats(Set.of(1, 5)));
        assertTrue(ticket.isPurchaserUserName(JOSEUSER_USERNAME));
        assertTrue(fakePaymenentProvider.hasBeanCalledWith(
                JOSEUSER_CREDIT_CARD_NUMBER,
                JOSEUSER_CREDIT_CARD_EXPIRITY, JOSEUSER_CREDIT_CARD_SEC_CODE,
                ticket.total()));
        var emailTemplate = new NewSaleEmailTemplate(ticket.total(),
                JOSEUSER_USERNAME, Set.of(1, 5), SUPER_MOVIE_NAME,
                new FormattedDayTime(LocalDateTime.of(
                        LocalDate.now().plusYears(1).getYear(), 10, 10, 13, 30))
                        .toString());
        assertTrue(fakeEmailProvider.hasBeanCalledWith(JOSEUSER_EMAIL,
                emailTemplate.subject(), emailTemplate.body()));
        var detailedShow = cinema.show(showInfo.showId());
        assertTrue(detailedShow.currentSeats().contains(new Seat(1, false)));
        assertTrue(detailedShow.currentSeats().contains(new Seat(2, true)));
        assertTrue(detailedShow.currentSeats().contains(new Seat(3, true)));
        assertTrue(detailedShow.currentSeats().contains(new Seat(4, true)));
        assertTrue(detailedShow.currentSeats().contains(new Seat(5, false)));
    }

    @ParameterizedTest
    @MethodSource("createCinema")
    public void rateMovie(CinemaSystem cinema) {
        var movieInfo = tests.createSuperMovie(cinema);
        var joseId = registerUserJose(cinema);
        var userRate = cinema.rateMovieBy(joseId, movieInfo.id(), 4,
                "great movie");
        assertEquals(JOSEUSER_USERNAME, userRate.username());
        assertEquals(4, userRate.rateValue());
    }

    @ParameterizedTest
    @MethodSource("createCinema")
    public void retrieveRatesInvalidPageNumber(CinemaSystem cinema) {
        var e = assertThrows(BusinessException.class, () -> {
            cinema.pagedRatesOfOrderedDate("abc", 0);
        });
        assertEquals(Cinema.PAGE_NUMBER_MUST_BE_GREATER_THAN_ZERO,
                e.getMessage());
    }

    @ParameterizedTest
    @MethodSource("createCinemaPageSize2")
    public void retrievePagedRatesFromMovie(CinemaSystem cinema) {
        var movieInfo = tests.createSuperMovie(cinema);
        var joseId = registerUserJose(cinema);
        var userId = registerAUser(cinema);
        var antonioId = registerUserAntonio(cinema);
        cinema.rateMovieBy(userId, movieInfo.id(), 1, "very bad movie");
        cinema.rateMovieBy(joseId, movieInfo.id(), 2, "bad movie");
        cinema.rateMovieBy(antonioId, movieInfo.id(), 3, "regular movie");
        var userRates = cinema.pagedRatesOfOrderedDate(movieInfo.id(), 1);
        assertEquals(2, userRates.size());
        assertEquals(ANTONIOUSER_USERNAME, userRates.get(0).username());
        assertEquals(JOSEUSER_USERNAME, userRates.get(1).username());
    }

    @ParameterizedTest
    @MethodSource("createCinema")
    public void rateSameMovieByThreeUsers(CinemaSystem cinema) {
        var movieInfo = tests.createSuperMovie(cinema);
        var joseId = registerUserJose(cinema);
        var antonioId = registerUserAntonio(cinema);
        var aUserId = registerAUser(cinema);
        cinema.rateMovieBy(joseId, movieInfo.id(), 4,
                "great movie");
        cinema.rateMovieBy(antonioId, movieInfo.id(), 2,
                "bad movie");
        cinema.rateMovieBy(aUserId, movieInfo.id(), 5,
                "fantastic movie");
        var movie = cinema.movie(movieInfo.id());
        var listOfRates = cinema.pagedRatesOfOrderedDate(movieInfo.id(), 1);
        assertEquals(3, listOfRates.size());
        assertEquals(String.valueOf(3.67f), movie.ratingValue());
    }

    @ParameterizedTest
    @MethodSource("createCinema")
    public void retrieveAllPagedRates(CinemaSystem cinema) {
        var superMovieInfo = tests.createSuperMovie(cinema);
        var otherMovieInfo = tests.createOtherSuperMovie(cinema);
        var joseId = registerUserJose(cinema);
        cinema.rateMovieBy(joseId, superMovieInfo.id(), 1, "very bad movie");
        cinema.rateMovieBy(joseId, otherMovieInfo.id(), 3, "fine movie");
        var movies = cinema.pagedMoviesSortedByRate(1);
        assertEquals(2, movies.size());
        assertEquals(ForTests.OTHER_SUPER_MOVIE_NAME, movies.get(0).name());
        assertEquals(ForTests.SUPER_MOVIE_NAME, movies.get(1).name());
    }

    @ParameterizedTest
    @MethodSource("createCinema")
    public void rateTheSameMovieTwice(CinemaSystem cinema) {
        var movieInfo = tests.createSuperMovie(cinema);
        var joseId = registerUserJose(cinema);
        cinema.rateMovieBy(joseId, movieInfo.id(), 4, "great movie");
        var e = assertThrows(BusinessException.class, () -> {
            cinema.rateMovieBy(joseId, movieInfo.id(), 4, "great movie");
            fail("I was able to rate the same movie twice");
        });
        assertEquals(Cinema.USER_HAS_ALREADY_RATE, e.getMessage());
    }

    @ParameterizedTest
    @MethodSource("createCinema")
    public void retrieveMovie(CinemaSystem cinema) {
        var superMovie = tests.createSuperMovie(cinema);
        MovieInfo movie = cinema.movie(superMovie.id());
        assertEquals(2, movie.actors().size());
        assertEquals(1, movie.directorNames().size());
        assertEquals(SUPER_MOVIE_DIRECTOR_NAME, movie.directorNames().get(0));
        assertTrue(movie.actors()
                .contains(SUPER_MOVIE_ACTOR_CARLOS));
        assertEquals(SUPER_MOVIE_NAME, movie.name());
        assertEquals(SUPER_MOVIE_PLOT, movie.plot());
    }

    @ParameterizedTest
    @MethodSource("createCinemaPageSize1")
    public void moviesSortedByReleaseDate(CinemaSystem cinema) {
        tests.createSuperMovie(cinema);
        tests.createOtherSuperMovie(cinema);
        var movies = cinema.pagedMoviesSortedByReleaseDate(1);
        assertEquals(1, movies.size());
        assertEquals(SUPER_MOVIE_NAME, movies.get(0).name());
    }

    @ParameterizedTest
    @MethodSource("createCinemaPageSize1")
    public void retrieveAllMovies(CinemaSystem cinema) {
        tests.createSuperMovie(cinema);
        tests.createOtherSuperMovie(cinema);
        var movies1 = cinema.pagedMoviesSortedByName(1);
        assertEquals(1, movies1.size());
        assertEquals(SUPER_MOVIE_NAME, movies1.get(0).name());
        assertEquals(2, movies1.get(0).genres().size());
        assertEquals(2, movies1.get(0).actors().size());
        var movies2 = cinema.pagedMoviesSortedByName(2);
        assertEquals(1, movies2.size());
        assertEquals(OTHER_SUPER_MOVIE_NAME, movies2.get(0).name());
        assertEquals(2, movies2.get(0).genres().size());
        assertEquals(1, movies2.get(0).actors().size());
    }

    @ParameterizedTest
    @MethodSource("createCinema")
    public void searchMovieByName(CinemaSystem cinema) {
        tests.createSuperMovie(cinema);
        tests.createOtherSuperMovie(cinema);
        var movies = cinema.pagedSearchMovieByName("another", 1);
        assertEquals(1, movies.size());
        assertEquals(OTHER_SUPER_MOVIE_NAME, movies.get(0).name());
    }

    @Test
    public void reservationHasExpired() {
        var cinema = new TxJpaCinema(emf, tests.doNothingPaymentProvider(),
                tests.doNothingEmailProvider(),
                tests.doNothingToken(),
                // already in the past
                () -> LocalDateTime.now().minusMonths(1),
                10);
        var movieInfo = tests.createSuperMovie(cinema);
        String theaterId = createATheater(cinema);
        var showInfo = cinema.addNewShowFor(movieInfo.id(),
                LocalDateTime.of(LocalDate.now().plusYears(1).getYear(), 10, 10,
                        13, 30),
                10f, theaterId, 20);
        var userId = registerUserJose(cinema);
        cinema.reserve(userId, showInfo.showId(), Set.of(1, 5));
        var e = assertThrows(BusinessException.class, () -> {
            cinema.pay(userId, showInfo.showId(), Set.of(1, 5),
                    JOSEUSER_CREDIT_CARD_NUMBER,
                    JOSEUSER_CREDIT_CARD_EXPIRITY,
                    JOSEUSER_CREDIT_CARD_SEC_CODE);
        });
        assertEquals("Reservation is required before confirm", e.getMessage());
    }

    @ParameterizedTest
    @MethodSource("createCinema")
    public void searchMovieByNameNotFound(CinemaSystem cinema) {
        tests.createSuperMovie(cinema);
        tests.createOtherSuperMovie(cinema);
        var movies = cinema.pagedSearchMovieByName("not_found_movie", 1);
        assertEquals(0, movies.size());
    }

    @ParameterizedTest
    @MethodSource(value = "createCinema")
    public void userChangePasswordDoesNotMatch(CinemaSystem cinema) {
        var userId = registerUserJose(cinema);

        var e = assertThrows(BusinessException.class, () -> {
            cinema.changePassword(userId, JOSEUSER_PASS, "123412341234",
                    "123412341294");
        });
        assertTrue(e.getMessage().equals(User.PASSWORDS_MUST_BE_EQUALS));
    }

    @ParameterizedTest
    @MethodSource(value = "createCinema")
    public void userProfileFrom(CinemaSystem cinema) {
        var userId = registerUserJose(cinema);

        var profile = cinema.profileFrom(userId);
        assertEquals(JOSEUSER_USERNAME, profile.username());
        assertEquals(JOSEUSER_EMAIL, profile.email());
        assertEquals(JOSEUSER_NAME + " " + JOSEUSER_SURNAME,
                profile.fullname());
    }

    private String registerUserJose(CinemaSystem cinema) {
        var joseId = cinema.registerUser(JOSEUSER_NAME, JOSEUSER_SURNAME,
                JOSEUSER_EMAIL,
                JOSEUSER_USERNAME,
                JOSEUSER_PASS, JOSEUSER_PASS);
        return joseId;
    }

    private String registerUserAntonio(CinemaSystem cinema) {
        var userId = cinema.registerUser("Antonio", "Antonio Surname",
                "antonio@bla.com",
                ANTONIOUSER_USERNAME,
                "password12345678", "password12345678");
        return userId;
    }

    private String registerAUser(CinemaSystem cinema) {
        var userId = cinema.registerUser("aUser", "user surname",
                "enrique@bla.com",
                "username",
                "password12345678", "password12345678");
        return userId;
    }

    private String createATheater(CinemaSystem cinema) {
        return cinema.addNewTheater("a Theater",
                Set.of(1, 2, 3, 4, 5, 6));
    }

    @AfterEach
    public void tearDown() {
        emf.close();
    }

}
