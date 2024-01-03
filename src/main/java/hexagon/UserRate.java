package hexagon;

import java.time.LocalDateTime;
import java.util.UUID;

import hexagon.primary.port.BusinessException;
import hexagon.primary.port.UserMovieRate;

public class UserRate {

	static final String INVALID_RATING = "Rate value must be an integer value between 0 and 5";
	private UUID id;
	private User user;
	private int value;
	private String comment;
	private Movie movie;
	private LocalDateTime ratedAt;

	public UserRate(String id, User user, int value, String comment,
			Movie movie) {
		this(user, value, comment, movie);
		this.id = UUID.fromString(id);
	}

	public UserRate(User user, int value, String comment, Movie movie) {
		checkValidRateValue(value);
		this.id = UUID.randomUUID();
		this.user = user;
		this.value = value;
		this.comment = comment;
		this.movie = movie;
		this.ratedAt = LocalDateTime.now();
	}

	private void checkValidRateValue(int value) {
		if (value < 0 || value > 5) {
			throw new BusinessException(INVALID_RATING);
		}
	}

	public boolean isRatedBy(User aUser) {
		return this.user.equals(aUser);
	}

	public UserMovieRate toUserMovieRate() {
		return new UserMovieRate(this.user.userName(), value,
				new FormattedDateTime(ratedAt).toString(), comment);
	}

	public String id() {
		return id.toString();
	}

	public User user() {
		return user;
	}

	public int rateValue() {
		return value;
	}

	public String comment() {
		return comment;
	}

	public Movie getMovie() {
		return movie;
	}

	public LocalDateTime ratedAt() {
		return ratedAt;
	}

}
