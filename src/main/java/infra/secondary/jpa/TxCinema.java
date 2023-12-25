package infra.secondary.jpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import hexagon.Cinema;
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
import hexagon.secondary.port.ForSendingEmailNotifications;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class TxCinema implements CinemaSystem {

	private EntityManagerFactory emf;
	private ForManagingCreditCardPayments forPayments;
	private ForSendingEmailNotifications forSendingEmails;
	private Token token;
	private DateTimeProvider timeProvider;
	private int pageSize = 10;

	public TxCinema(EntityManagerFactory emf,
			ForManagingCreditCardPayments forPayments,
			ForSendingEmailNotifications forSendingEmails, Token token,
			DateTimeProvider timeProvider) {
		this.emf = emf;
		this.forPayments = forPayments;
		this.forSendingEmails = forSendingEmails;
		this.token = token;
		this.timeProvider = timeProvider;
	}

	public TxCinema(EntityManagerFactory emf,
			ForManagingCreditCardPayments forPayments,
			ForSendingEmailNotifications forSendingEmails, Token token,
			DateTimeProvider timeProvider, int pageSize) {
		this(emf, forPayments, forSendingEmails, token, timeProvider);
		this.pageSize = pageSize;
	}

	@Override
	public List<MovieShows> showsUntil(LocalDateTime untilTo) {
		return inTx(em -> {

			// var cinema = new Cinema();
			//
			// this.cinema.showsUntil(untilTo);

			return null;
		});
	}

	@Override
	public List<MovieInfo> pagedMoviesSortedByName(int pageNumber) {
		return inTx(em -> {
			var cinema = createCinema(em);
			return cinema.pagedMoviesSortedByName(pageNumber);
		});
	}

	@Override
	public List<MovieInfo> pagedMoviesSortedByRate(int pageNumber) {
		return inTx(em -> {
			var cinema = createCinema(em);
			return cinema.pagedMoviesSortedByRate(pageNumber);
		});
	}

	@Override
	public List<MovieInfo> pagedMoviesSortedByReleaseDate(int pageNumber) {
		return inTx(em -> {
			var cinema = createCinema(em);
			return cinema.pagedMoviesSortedByReleaseDate(pageNumber);
		});
	}

	@Override
	public MovieInfo movie(String id) {
		return inTx(em -> {
			// or have Cinema already instantiated and use a setter for
			// or instantiate every time. For me is better this...
			// always create ready to use instances, better for understanding
			var cinema = createCinema(em);
			return cinema.movie(id);
		});
	}

	@Override
	public DetailedShowInfo show(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MovieInfo addNewMovie(String name, int duration,
			LocalDate releaseDate, String plot, Set<Genre> genres) {
		return inTx(em -> {
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
				this.timeProvider, this.token, 10);
	}

	@Override
	public MovieInfo addActorTo(String movieId, String name, String surname,
			String email, String characterName) {
		return inTx(em -> {
			var cinema = createCinema(em);
			return cinema.addActorTo(movieId, name, surname,
					email, characterName);
		});

	}

	@Override
	public MovieInfo addDirectorTo(String movieId, String name,
			String surname, String email) {
		return inTx(em -> {
			var cinema = createCinema(em);
			return cinema.addDirectorTo(movieId, name, surname,
					email);
		});
	}

	@Override
	public String addNewTheater(String name, Set<Integer> seatsNumbers) {
		return inTx(em -> {
			var cinema = createCinema(em);
			return cinema.addNewTheater(name, seatsNumbers);
		});
	}

	@Override
	public ShowInfo addNewShowFor(Long movieId, LocalDateTime startTime,
			float price, Long theaterId, int pointsToWin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DetailedShowInfo reserve(Long userId, Long showTimeId,
			Set<Integer> selectedSeats) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ticket pay(Long userId, Long showTimeId, Set<Integer> selectedSeats,
			String creditCardNumber, YearMonth expirationDate,
			String secturityCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserMovieRate rateMovieBy(Long userId, Long idMovie, int rateValue,
			String comment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserMovieRate> pagedRatesOfOrderedDate(Long movieId,
			int pageNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MovieInfo> pagedSearchMovieByName(String fullOrPartmovieName,
			int pageNumber) {
		return inTx(em -> {
			var cinema = createCinema(em);
			return cinema.pagedSearchMovieByName(fullOrPartmovieName,
					pageNumber);
		});
	}

	@Override
	public String login(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long userIdFrom(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changePassword(Long userId, String currentPassword,
			String newPassword1, String newPassword2) {
		// TODO Auto-generated method stub

	}

	@Override
	public UserProfile profileFrom(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long registerUser(String name, String surname, String email,
			String userName, String password, String repeatPassword) {
		// TODO Auto-generated method stub
		return null;
	}

	private <T> T inTx(Function<EntityManager, T> toExecute) {
		var em = this.emf.createEntityManager();
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
}
