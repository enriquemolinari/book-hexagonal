package infra.secondary.jpa.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
@Table(uniqueConstraints = {
		@UniqueConstraint(name = "USER_CANT_RATE_A_MOVIE_MORE_THAN_ONCE", columnNames = {
				"movie_id", "user_id"})})
class UserRate {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	private int value;
	private String comment;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "movie_id")
	private MovieEntity movie;
	private LocalDateTime ratedAt;

	public UserRate(User user, int value, String comment, MovieEntity movie) {
		this.user = user;
		this.value = value;
		this.comment = comment;
		this.movie = movie;
		this.ratedAt = LocalDateTime.now();
	}

	public boolean isRatedBy(User aUser) {
		return this.user.equals(aUser);
	}

	// public UserMovieRate toUserMovieRate() {
	// return new UserMovieRate(this.user.userName(), value,
	// new FormattedDateTime(ratedAt).toString(), comment);
	// }
}
