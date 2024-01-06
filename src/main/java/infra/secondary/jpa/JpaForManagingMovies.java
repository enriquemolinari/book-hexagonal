package infra.secondary.jpa;

import hexagon.Movie;
import hexagon.UserRate;
import hexagon.secondary.port.ForManagingMovies;
import infra.secondary.jpa.entities.MovieEntity;
import infra.secondary.jpa.entities.UserRateEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class JpaForManagingMovies implements ForManagingMovies {

    private final EntityManager em;
    private final int pageSize;

    public JpaForManagingMovies(EntityManager em, int pageSize) {
        this.em = em;
        this.pageSize = pageSize;
    }

    @Override
    public Movie movieBy(String id) {
        var movieEntity = movieEntityById(id);
        return movieEntity.toDomain();
    }

    @Override
    public void newMovie(Movie movie) {
        MovieEntity entity = MovieEntity.fromDomain(movie);
        em.persist(entity);
    }

    @Override
    public void addDirector(String movieId, String directorId, String name,
                            String surname,
                            String email) {
        var movieEntity = movieEntityById(movieId);
        movieEntity.addADirector(directorId, name, surname, email);
    }

    private MovieEntity movieEntityById(String movieId) {
        return em.getReference(MovieEntity.class, UUID.fromString(movieId));
    }

    @Override
    public void addAnActor(String movieId, String actorId, String name,
                           String surname,
                           String email, String characterName) {
        var movieEntity = movieEntityById(movieId);
        movieEntity.addAnActor(actorId, name, surname, email, characterName);
    }

    @Override
    public List<Movie> pagedSearchMovieByName(String fullOrPartmovieName,
                                              int pageNumber) {
        var q = em.createQuery(
                "select m from MovieEntity m "
                        // a trigram index is required
                        // on m.name to make this perform fine
                        + "where lower(m.name) like lower(?1) "
                        + "order by m.name desc",
                MovieEntity.class);
        q.setParameter(1, "%" + fullOrPartmovieName + "%");
        q.setFirstResult((pageNumber - 1) * this.pageSize);
        q.setMaxResults(this.pageSize);
        return movieEntitiesToDomain(q);
    }

    private List<Movie> movieEntitiesToDomain(TypedQuery<MovieEntity> q) {
        return q.getResultList().stream().map(MovieEntity::toDomain).toList();
    }

    @Override
    public List<Movie> pagedMoviesSortedByRate(int pageNumber) {
        return pagedMoviesSortedBy(pageNumber,
                "order by m.totalUserVotes desc, m.rateValue desc");
    }

    @Override
    public List<Movie> pagedMoviesSortedByName(int pageNumber) {
        return pagedMoviesSortedBy(pageNumber, "order by m.name");
    }

    @Override
    public List<Movie> pagedMoviesSortedByReleaseDate(int pageNumber) {
        return pagedMoviesSortedBy(pageNumber, "order by m.releaseDate desc");
    }

    private List<Movie> pagedMoviesSortedBy(int pageNumber,
                                            String orderByClause) {
        var q = em.createQuery(
                "select m from MovieEntity m "
                        + orderByClause,
                MovieEntity.class);
        q.setFirstResult((pageNumber - 1) * this.pageSize);
        q.setMaxResults(this.pageSize);

        return movieEntitiesToDomain(q);
    }

    @Override
    public void updateRating(Movie movie) {
        var me = em.getReference(MovieEntity.class,
                UUID.fromString(movie.id()));
        me.addUserRateEntity(movie.getUserRates().stream()
                .map(UserRateEntity::fromDomain)
                .toList());
        me.newRateValues(movie.totalUserVotes(), movie.currentRateValue(),
                movie.totalRate());
    }

    @Override
    public List<UserRate> pagedRatesOrderedByDate(String movieId,
                                                  int pageNumber) {
        var q = em.createQuery(
                "select ur from UserRateEntity ur "
                        + "where ur.movie.id = ?1 "
                        + "order by ur.ratedAt desc",
                UserRateEntity.class);
        q.setParameter(1, UUID.fromString(movieId));
        q.setFirstResult((pageNumber - 1) * this.pageSize);
        q.setMaxResults(this.pageSize);
        return q.getResultList().stream().map(ur -> ur.toDomain()).toList();
    }

    @Override
    public boolean doesThisUserRateTheMovie(String userId,
                                            String movieId) {
        var q = this.em.createQuery(
                "select ur from UserRateEntity ur where ur.user.id = ?1 and movie.id = ?2",
                UserRateEntity.class);
        q.setParameter(1, UUID.fromString(userId));
        q.setParameter(2, UUID.fromString(movieId));
        return !q.getResultList().isEmpty();
    }

    @Override
    public List<Movie> moviesWithShowsUntil(LocalDateTime untilTo) {
        var q = em.createQuery(
                        "from MovieEntity m "
                                + "join fetch m.showTimes s join fetch s.screenedIn "
                                + "where s.startTime >= ?1 and s.startTime <= ?2 "
                                + "order by m.name asc",
                        MovieEntity.class).setParameter(1, LocalDateTime.now())
                .setParameter(2, untilTo);
        return movieEntitiesToDomain(q);
    }
}
