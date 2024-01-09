package hexagon.primary.port;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

public interface CinemaSystem {

    List<MovieShows> showsUntil(LocalDateTime untilTo);

    List<MovieInfo> pagedMoviesSortedByName(int pageNumber);

    List<MovieInfo> pagedMoviesSortedByRate(int pageNumber);

    List<MovieInfo> pagedMoviesSortedByReleaseDate(int pageNumber);

    MovieInfo movie(String id);

    DetailedShowInfo show(String id);

    MovieInfo addNewMovie(String name, int duration,
                          LocalDate releaseDate, String plot, Set<Genre> genres);

    MovieInfo addActorTo(String movieId, String name, String surname,
                         String email, String characterName);

    MovieInfo addDirectorTo(String movieId, String name,
                            String surname, String email);

    String addNewTheater(String name, Set<Integer> seatsNumbers);

    ShowInfo addNewShowFor(String movieId, LocalDateTime startTime,
                           float price, String theaterId, int pointsToWin);

    DetailedShowInfo reserve(String userId, String showTimeId,
                             Set<Integer> selectedSeats);

    Ticket pay(String userId, String showTimeId, Set<Integer> selectedSeats,
               String creditCardNumber, YearMonth expirationDate,
               String secturityCode);

    UserMovieRate rateMovieBy(String userId, String idMovie, int rateValue,
                              String comment);

    List<UserMovieRate> pagedRatesOfOrderedDate(String movieId, int pageNumber);

    List<MovieInfo> pagedSearchMovieByName(String fullOrPartmovieName,
                                           int pageNumber);

    String login(String username, String password);

    String userIdFrom(String token);

    void changePassword(String userId, String currentPassword,
                        String newPassword1,
                        String newPassword2);

    UserProfile profileFrom(String userId);

    String registerUser(String name, String surname, String email,
                        String userName,
                        String password, String repeatPassword);

}
