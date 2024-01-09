package infra.secondary.inmemory;

import hexagon.Movie;
import hexagon.UserRate;
import hexagon.secondary.port.ForManagingMovies;

import java.time.LocalDateTime;
import java.util.*;

public class HashMapForManagingMovies implements ForManagingMovies {
    private final Map<String, Movie> moviesById;
    private final int pageSize;

    public HashMapForManagingMovies(int pageSize) {
        this.pageSize = pageSize;
        this.moviesById = new HashMap<>();
    }

    @Override
    public Optional<Movie> movieBy(String id) {
        return Optional.ofNullable(this.moviesById.get(id));
    }

    @Override
    public void newMovie(Movie movie) {
        this.moviesById.put(movie.id(), movie);
    }

    @Override
    public void addDirector(String movieId, String directorId, String name, String surname, String email) {
        //not required to do nothing else
    }

    @Override
    public void addAnActor(String movieId, String actorId, String name, String surname, String email, String characterName) {
        //not required to do nothing else
    }

    @Override
    public List<Movie> pagedMoviesSortedByReleaseDate(int pageNumber) {
        var movies = new ArrayList<>(this.moviesById.values());
        movies.sort(Comparator.comparing(Movie::getReleaseDate).reversed());
        return getPageOf(pageNumber, movies);
    }

    @Override
    public List<Movie> pagedMoviesSortedByName(int pageNumber) {
        var movies = new ArrayList<>(this.moviesById.values());
        movies.sort(Comparator.comparing(Movie::getName));
        return getPageOf(pageNumber, movies);
    }

    private <T> List<T> getPageOf(int pageNumber, List<T> elements) {
        int sIndex = Math.max(this.pageSize * (pageNumber - 1), 0);
        int eIndex = Math.min(sIndex + this.pageSize, elements.size());
        return elements.subList(sIndex, eIndex);
    }

    @Override
    public List<Movie> pagedMoviesSortedByRate(int pageNumber) {
        var movies = new ArrayList<>(this.moviesById.values());
        movies.sort(Comparator.comparing(Movie::currentRateValue).reversed());
        return getPageOf(pageNumber, movies);
    }

    @Override
    public List<Movie> pagedSearchMovieByName(String fullOrPartmovieName, int pageNumber) {
        var movies = this.moviesById.values();
        var filteredMovies = movies.stream().filter(m -> m.isNamedLike(fullOrPartmovieName)).toList();
        return getPageOf(pageNumber, filteredMovies);
    }

    @Override
    public void updateRating(Movie movie) {
        //not required to do nothing else
    }

    @Override
    public List<UserRate> pagedRatesOrderedByDate(String movieId, int pageNumber) {
        var movie = this.moviesById.get(movieId);
        var userRates = movie.getUserRates();
        userRates.sort(Comparator.comparing(UserRate::ratedAt).reversed());
        return getPageOf(pageNumber, userRates);
    }

    @Override
    public boolean doesThisUserRateTheMovie(String userId, String movieId) {
        var movie = this.moviesById.get(movieId);
        return movie.hasBeenRatedByUserWithId(userId);
    }

    @Override
    public List<Movie> moviesWithShowsUntil(LocalDateTime untilTo) {
        var movies = this.moviesById.values();
        var now = LocalDateTime.now();
        var result = movies.stream().filter(m -> m.hasShowsBetween(now, untilTo)).toList();
        return result.stream().map(m -> {
            var showsBetween = m.showsUntil(now, untilTo);
            var movie = new Movie(m.id(), m.getName(), m.getPlot(), m.getDuration(), m.getReleaseDate(),
                    m.getGenres(), m.getActors(), m.getDirectors(), m.totalUserVotes(),
                    m.currentRateValue(), m.totalRate());
            movie.showTimes(showsBetween);
            return movie;
        }).toList();
    }
}
