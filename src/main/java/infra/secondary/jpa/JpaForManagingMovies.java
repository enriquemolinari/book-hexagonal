package infra.secondary.jpa;

import java.util.List;
import java.util.UUID;

import hexagon.Movie;
import hexagon.secondary.port.ForManagingMovies;
import infra.secondary.jpa.entities.MovieEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class JpaForManagingMovies implements ForManagingMovies {

	private EntityManager em;
	private int pageSize;

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
		MovieEntity entity = new MovieEntity(movie.id(), movie.name(),
				movie.plot(),
				movie.duration(), movie.releaseDate(),
				movie.genres());
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
		return q.getResultList().stream().map(me -> me.toDomain()).toList();
	}

	@Override
	public List<Movie> pagedMoviesSortedByRate(int pageNumber) {
		return pagedMoviesSortedBy(pageNumber,
				"order by m.rating.totalUserVotes desc, m.rating.rateValue desc");
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

}
