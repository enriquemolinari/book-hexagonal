package hexagon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import hexagon.primary.port.AuthException;
import hexagon.primary.port.BusinessException;
import hexagon.primary.port.CinemaSystem;
import hexagon.primary.port.DateTimeProvider;
import hexagon.primary.port.DetailedShowInfo;
import hexagon.primary.port.Genre;
import hexagon.primary.port.MovieInfo;
import hexagon.primary.port.MovieShows;
import hexagon.primary.port.ShowInfo;
import hexagon.primary.port.Ticket;
import hexagon.primary.port.Token;
import hexagon.primary.port.UserMovieRate;
import hexagon.primary.port.UserProfile;
import hexagon.secondary.port.ForManagingCreditCardPayments;
import hexagon.secondary.port.ForManagingMovies;
import hexagon.secondary.port.ForManagingShows;
import hexagon.secondary.port.ForManagingUsers;
import hexagon.secondary.port.ForSendingEmailNotifications;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.RollbackException;

public class Cinema implements CinemaSystem {
	static final String USER_NAME_ALREADY_EXISTS = "userName already exists";
	private static final int NUMBER_OF_RETRIES = 2;
	static final String MOVIE_ID_DOES_NOT_EXISTS = "Movie ID not found";
	static final String SHOW_TIME_ID_NOT_EXISTS = "Show ID not found";
	static final String USER_ID_NOT_EXISTS = "User not registered";
	static final String CREDIT_CARD_DEBIT_HAS_FAILED = "Credit card debit have failed";
	static final String USER_HAS_ALREADY_RATE = "The user has already rate the movie";
	static final String PAGE_NUMBER_MUST_BE_GREATER_THAN_ZERO = "page number must be greater than zero";
	private static final int DEFAULT_PAGE_SIZE = 20;
	public static final String USER_OR_PASSWORD_ERROR = "Invalid username or password";

	private EntityManagerFactory emf;
	private ForManagingCreditCardPayments paymentGateway;
	private ForSendingEmailNotifications emailProvider;
	private EntityManager em;
	private int pageSize;
	private DateTimeProvider dateTimeProvider;
	private Token token;
	private ForManagingMovies forManagingMovies;
	private ForManagingShows forManagingShows;
	private ForManagingUsers forManagingUsers;

	// TODO: fix this, sacar emf
	public Cinema(ForManagingMovies managingMovies,
			ForManagingShows managingShows,
			ForManagingUsers managingUsers,
			ForManagingCreditCardPayments paymentGateway,
			ForSendingEmailNotifications emailProvider,
			DateTimeProvider provider,
			Token token,
			// TODO: sacar pageSize...
			int pageSize) {
		this.forManagingMovies = managingMovies;
		this.forManagingShows = managingShows;
		this.forManagingUsers = managingUsers;
		this.emf = null;
		this.paymentGateway = paymentGateway;
		this.emailProvider = emailProvider;
		this.token = token;
		this.pageSize = pageSize;
		this.dateTimeProvider = provider;
	}

	public Cinema(EntityManagerFactory emf,
			ForManagingCreditCardPayments paymentGateway,
			ForSendingEmailNotifications emailProvider,
			DateTimeProvider provider,
			Token token,
			int pageSize) {
		this.emf = emf;
		this.paymentGateway = paymentGateway;
		this.emailProvider = emailProvider;
		this.token = token;
		this.pageSize = pageSize;
		this.dateTimeProvider = provider;
	}

	public Cinema(EntityManagerFactory emf,
			ForManagingCreditCardPayments paymentGateway,
			ForSendingEmailNotifications emailProvider, Token token,
			int pageSize) {
		this(emf, paymentGateway, emailProvider, DateTimeProvider.create(),
				token, pageSize);
	}

	public Cinema(EntityManagerFactory emf,
			ForManagingCreditCardPayments paymentGateway,
			ForSendingEmailNotifications emailProvider, Token token) {
		this(emf, paymentGateway, emailProvider, DateTimeProvider.create(),
				token, DEFAULT_PAGE_SIZE);
	}

	@Override
	public List<MovieShows> showsUntil(LocalDateTime untilTo) {
		return inTx(em -> {
			return movieShowsUntil(untilTo);
		});
	}

	private List<MovieShows> movieShowsUntil(LocalDateTime untilTo) {
		var query = em.createQuery(
				"from Movie m "
						+ "join fetch m.showTimes s join fetch s.screenedIn "
						+ "where s.startTime >= ?1 and s.startTime <= ?2 "
						+ "order by m.name asc",
				Movie.class).setParameter(1, LocalDateTime.now())
				.setParameter(2, untilTo);

		return query.getResultList().stream()
				.map(movieShow -> movieShow.toMovieShow())
				.collect(Collectors.toUnmodifiableList());
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
	public ShowInfo addNewShowFor(Long movieId, LocalDateTime startTime,
			float price, Long theaterId, int pointsToWin) {
		return inTx(em -> {
			var movie = movieBy(movieId);
			var theatre = theatreBy(theaterId);

			var showTime = new ShowTime(movie, startTime, price, theatre,
					pointsToWin);

			em.persist(showTime);

			return showTime.toShowInfo();
		});
	}

	@Override
	public DetailedShowInfo reserve(Long userId, Long showTimeId,
			Set<Integer> selectedSeats) {
		return inTx(em -> {

			ShowTime showTime = showTimeBy(showTimeId);
			var user = userBy(userId);

			showTime.reserveSeatsFor(user, selectedSeats);

			return showTime.toDetailedInfo();
		});
	}

	@Override
	public Ticket pay(Long userId, Long showTimeId, Set<Integer> selectedSeats,
			String creditCardNumber, YearMonth expirationDate,
			String secturityCode) {

		return inTx(em -> {
			ShowTime showTime = showTimeBy(showTimeId);
			var user = userBy(userId);

			var totalAmount = confirmSeatsAndGetTotalAmount(selectedSeats,
					showTime, user);

			tryCreditCardDebit(creditCardNumber, expirationDate, secturityCode,
					totalAmount);

			sendNewSaleEmailToTheUser(selectedSeats, showTime, user,
					totalAmount);

			var sale = new Sale(totalAmount, user, showTime,
					showTime.pointsToEarn(), selectedSeats);

			return sale.ticket();
		});
	}

	@Override
	public String login(String username, String password) {
		return inTx(em -> {
			var q = this.em.createQuery(
					"select u from User u where u.userName = ?1 and u.password.password = ?2",
					User.class);
			q.setParameter(1, username);
			q.setParameter(2, password);
			var mightBeAUser = q.getResultList();
			if (mightBeAUser.size() == 0) {
				throw new AuthException(USER_OR_PASSWORD_ERROR);
			}
			var user = mightBeAUser.get(0);
			em.persist(new LoginAudit(this.dateTimeProvider.now(), user));
			return token.tokenFrom(user.toMap());
		});
	}

	@Override
	public Long registerUser(String name, String surname, String email,
			String userName,
			String password, String repeatPassword) {
		return inTxWithRetriesOnConflict((em) -> {
			checkUserNameAlreadyExists(userName);
			var user = new User(new Person(name, surname, email), userName,
					password,
					repeatPassword);
			em.persist(user);
			return user.id();
		}, NUMBER_OF_RETRIES);
	}

	@Override
	public UserMovieRate rateMovieBy(Long userId, Long movieId, int rateValue,
			String comment) {
		return inTxWithRetriesOnConflict(em -> {
			checkUserIsRatingSameMovieTwice(userId, movieId);
			var user = userBy(userId);
			var movie = movieBy(movieId);

			var userRate = movie.rateBy(user, rateValue, comment);
			return userRate.toUserMovieRate();
		}, NUMBER_OF_RETRIES);
	}

	private void checkUserIsRatingSameMovieTwice(Long userId, Long movieId) {
		var q = this.em.createQuery(
				"select ur from UserRate ur where ur.user.id = ?1 and movie.id = ?2",
				UserRate.class);
		q.setParameter(1, userId);
		q.setParameter(2, movieId);
		var mightHaveRated = q.getResultList();
		if (mightHaveRated.size() > 0) {
			throw new BusinessException(USER_HAS_ALREADY_RATE);
		}
	}

	private void checkUserNameAlreadyExists(String userName) {
		var q = this.em.createQuery(
				"select u from User u where u.userName = ?1 ", User.class);
		q.setParameter(1, userName);
		var mightBeAUser = q.getResultList();
		if (mightBeAUser.size() > 0) {
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
				user.userName(), selectedSeats, showTime.movieName(),
				showTime.startDateTime());

		this.emailProvider.send(user.email(), emailTemplate.subject(),
				emailTemplate.body());
	}

	private float confirmSeatsAndGetTotalAmount(Set<Integer> selectedSeats,
			ShowTime showTime, User user) {
		showTime.confirmSeatsForUser(user, selectedSeats);
		var totalAmount = showTime.totalAmountForTheseSeats(selectedSeats);
		return totalAmount;
	}

	private Theater theatreBy(Long theatreId) {
		try {
			return em.getReference(Theater.class, theatreId);
		} catch (IllegalArgumentException e) {
			throw new BusinessException(MOVIE_ID_DOES_NOT_EXISTS);
		}
	}

	private Movie movieBy(Long movieId) {
		return findByIdOrThrows(Movie.class, movieId, MOVIE_ID_DOES_NOT_EXISTS);
	}

	private User userBy(Long userId) {
		return findByIdOrThrows(User.class, userId, USER_ID_NOT_EXISTS);
	}

	private ShowTime showTimeBy(Long id) {
		return findByIdOrThrows(ShowTime.class, id, SHOW_TIME_ID_NOT_EXISTS);
	}

	<T> T findByIdOrThrows(Class<T> entity, Long id, String msg) {
		var e = em.find(entity, id);
		if (e == null) {
			throw new BusinessException(msg);
		}

		return e;
	}

	@Override
	public List<UserMovieRate> pagedRatesOfOrderedDate(Long movieId,
			int pageNumber) {
		checkPageNumberIsGreaterThanZero(pageNumber);
		return inTx(em -> {
			var q = em.createQuery(
					"select ur from UserRate ur "
							+ "where ur.movie.id = ?1 "
							+ "order by ur.ratedAt desc",
					UserRate.class);
			q.setParameter(1, movieId);
			q.setFirstResult((pageNumber - 1) * this.pageSize);
			q.setMaxResults(this.pageSize);
			return q.getResultList().stream()
					.map(rate -> rate.toUserMovieRate()).toList();
		});
	}

	@Override
	public DetailedShowInfo show(Long id) {
		return inTx(em -> {
			var show = showTimeBy(id);
			return show.toDetailedInfo();
		});
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

	// TODO: sacar de aca...
	private <T> T inTx(Function<EntityManager, T> toExecute) {
		em = emf.createEntityManager();
		var tx = em.getTransaction();

		try {
			tx.begin();

			T t = toExecute.apply(em);
			tx.commit();

			return t;
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	private <T> T inTxWithRetriesOnConflict(
			Function<EntityManager, T> toExecute,
			int retriesNumber) {
		int retries = 0;

		while (retries < retriesNumber) {
			try {
				return inTx(toExecute);
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

	@Override
	public Long userIdFrom(String token) {
		return this.token.verifyAndGetUserIdFrom(token);
	}

	@Override
	public UserProfile profileFrom(Long userId) {
		return inTx(em -> {
			return userBy(userId).toProfile();
		});
	}

	@Override
	public void changePassword(Long userId, String currentPassword,
			String newPassword1, String newPassword2) {
		inTx(em -> {
			userBy(userId).changePassword(currentPassword, newPassword1,
					newPassword2);
			// just to conform the compiler
			return null;
		});
	}
}