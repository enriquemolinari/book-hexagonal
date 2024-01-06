package hexagon.secondary.port;

import hexagon.Movie;
import hexagon.UserRate;

import java.time.LocalDateTime;
import java.util.List;

//TODO: Definir paquete donde poner lo compartido entre los puertos y el hexagon
public interface ForManagingMovies {

    Movie movieBy(String id);

    void newMovie(Movie movie);

    void addDirector(String movieId, String directorId, String name,
                     String surname, String email);

    void addAnActor(String movieId, String actorId, String name, String surname,
                    String email,
                    String characterName);

    List<Movie> pagedMoviesSortedByReleaseDate(int pageNumber);

    List<Movie> pagedMoviesSortedByName(int pageNumber);

    List<Movie> pagedMoviesSortedByRate(int pageNumber);

    List<Movie> pagedSearchMovieByName(String fullOrPartmovieName,
                                       int pageNumber);

    void updateRating(Movie movie);

    List<UserRate> pagedRatesOrderedByDate(String movieId, int pageNumber);

    boolean doesThisUserRateTheMovie(String userId,
                                     String movieId);

    List<Movie> moviesWithShowsUntil(LocalDateTime untilTo);
}
