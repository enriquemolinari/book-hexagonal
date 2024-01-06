package hexagon;

import hexagon.primary.port.*;
import hexagon.secondary.port.ForManagingCreditCardPayments;
import hexagon.secondary.port.ForSendingEmailNotifications;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ForTests {

    static final String SUPER_MOVIE_PLOT = "a super movie that shows the life of ...";
    static final String SUPER_MOVIE_NAME = "a super movie";
    static final String OTHER_SUPER_MOVIE_NAME = "another super movie";
    static final String SUPER_MOVIE_DIRECTOR_NAME = "aDirectorName surname";
    static final ActorInMovieName SUPER_MOVIE_ACTOR_CARLOS = new ActorInMovieName(
            "Carlos Kalchi",
            "aCharacterName");

    EmailProviderFake fakeEmailProvider() {
        return new EmailProviderFake();
    }

    PaymenentProviderFake fakePaymenentProvider() {
        return new PaymenentProviderFake();
    }

    String createEmailBody(String userName, String movieName,
                           Set<Integer> seats, String showTime, float amount) {
        var orderedListofSeats = new ArrayList<>(seats);
        Collections.sort(orderedListofSeats);

        var body = new StringBuilder();
        body.append("Hello ").append(userName).append("!");
        body.append(System.lineSeparator());
        body.append("You have new tickets!");
        body.append(System.lineSeparator());
        body.append("Here are the details of your booking: ");
        body.append(System.lineSeparator());
        body.append("Movie: ").append(movieName);
        body.append(System.lineSeparator());
        body.append("Seats: ").append(orderedListofSeats.stream()
                .map(s -> s.toString()).collect(Collectors.joining(",")));
        body.append(System.lineSeparator());
        body.append("Showtime: ").append(showTime);
        body.append(System.lineSeparator());
        body.append("Total paid: ").append(amount);

        return body.toString();
    }

    ShowTime createShowTime(ForManagingCreditCardPayments gProvider,
                            ForSendingEmailNotifications eProvider, int pointsToWin) {
        return new ShowTime(this.createSmallFishMovie(),
                LocalDateTime.now().plusDays(2), 10f, new Theater("a Theater",
                Set.of(1, 2, 3, 4, 5, 6)),
                pointsToWin);
    }

    Movie createSmallFishMovie() {
        return createSmallFishMovie(LocalDate.of(2023, 10, 10));
    }

    Movie createSmallFishMovie(LocalDate releaseDate) {
        return new Movie("Small Fish", "plot x", 102,
                releaseDate,
                Set.of(Genre.COMEDY, Genre.ACTION)/* genre */,
                List.of(new Actor(
                        new Person("aName", "aSurname", "anEmail@mail.com"),
                        "George Bix")),
                List.of(new Person("aDirectorName", "aDirectorSurname",
                        "anotherEmail@mail.com")));
    }

    ForSendingEmailNotifications doNothingEmailProvider() {
        return (to, subject, body) -> {
        };
    }

    ForManagingCreditCardPayments doNothingPaymentProvider() {
        return (creditCardNumber, expire, securityCode, totalAmount) -> {
        };
    }

    Token doNothingToken() {
        return new Token() {
            @Override
            public String verifyAndGetUserIdFrom(String token) {
                return "abc";
            }

            @Override
            public String tokenFrom(Map<String, Object> payload) {
                return "aToken";
            }
        };
    }

    Movie createSmallFishMovieWithRates() {
        return new Movie("Small Fish", "plot ...", 102,
                LocalDate.of(2023, 10, 10) /* release data */,
                Set.of(Genre.COMEDY, Genre.ACTION)/* genre */,
                List.of(new Actor(
                        new Person("aName", "aSurname", "anEmail@mail.com"),
                        "George Bix")),
                List.of(new Person("aDirectorName", "aDirectorSurname",
                        "anotherEmail@mail.com")));
    }

    ShowTime createShowForSmallFish(DateTimeProvider provider) {
        return new ShowTime(provider, createSmallFishMovie(),
                LocalDateTime.now().plusDays(1), 10f,
                new Theater("a Theater", Set.of(1, 2, 3, 4, 5, 6)));
    }

    ShowTime createShowForSmallFish() {
        return createShowForSmallFish(DateTimeProvider.create());
    }

    User createUserCharly() {
        return new User(new Person("Carlos", "Edgun", "cedgun@mysite.com"),
                "cedgun", "afbcdefghigg", "afbcdefghigg");
    }

    User createUserJoseph() {
        return new User(new Person("Joseph", "Valdun", "jvaldun@wabla.com"),
                "jvaldun", "tabcd1234igg", "tabcd1234igg");
    }

    User createUserNicolas() {
        return new User(
                new Person("Nicolas", "Molinari", "nmolinari@yesmy.com"),
                "nmolinari", "oneplayminebrawl", "oneplayminebrawl");
    }

    MovieInfo createSuperMovie(CinemaSystem cinema) {
        var movieInfo = cinema.addNewMovie(SUPER_MOVIE_NAME, 109,
                LocalDate.of(2023, 04, 05),
                SUPER_MOVIE_PLOT,
                Set.of(Genre.ACTION, Genre.ADVENTURE));

        cinema.addActorTo(movieInfo.id(), "Carlos", "Kalchi",
                "carlosk@bla.com", "aCharacterName");

        cinema.addActorTo(movieInfo.id(), "Jose", "Hermes",
                "jose@bla.com", "anotherCharacterName");

        cinema.addDirectorTo(movieInfo.id(), "aDirectorName", "surname",
                "adir@bla.com");

        return movieInfo;
    }

    MovieInfo createOtherSuperMovie(CinemaSystem cinema) {
        var movieInfo = cinema.addNewMovie(OTHER_SUPER_MOVIE_NAME, 80,
                LocalDate.of(2022, 04, 05),
                "other super movie ...",
                Set.of(Genre.COMEDY, Genre.FANTASY));

        cinema.addActorTo(movieInfo.id(), "Nico", "Cochix",
                "nico@bla.com", "super Character Name");

        cinema.addDirectorTo(movieInfo.id(), "aSuper DirectorName",
                "sur name",
                "asuper@bla.com");

        return movieInfo;
    }

}

class EmailProviderFake implements ForSendingEmailNotifications {
    private String to;
    private String subject;
    private String body;

    @Override
    public void send(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public boolean hasBeanCalledWith(String to, String subject, String body) {
        return this.to.equals(to) && this.subject.equals(subject)
                && this.body.equals(body);
    }
}

class PaymenentProviderFake implements ForManagingCreditCardPayments {
    private String creditCardNumber;
    private YearMonth expire;
    private String securityCode;
    private float totalAmount;

    @Override
    public void pay(String creditCardNumber, YearMonth expire,
                    String securityCode, float totalAmount) {
        this.creditCardNumber = creditCardNumber;
        this.expire = expire;
        this.securityCode = securityCode;
        this.totalAmount = totalAmount;
    }

    public boolean hasBeanCalledWith(String creditCardNumber, YearMonth expire,
                                     String securityCode, float totalAmount) {
        return this.creditCardNumber.equals(creditCardNumber)
                && this.expire.equals(expire)
                && this.securityCode.equals(securityCode)
                && this.totalAmount == totalAmount;
    }
}