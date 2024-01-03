package infra.secondary.jpa.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import hexagon.UserRate;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class UserRateEntity {
	@Id
	private UUID id;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;
	private int value;
	private String comment;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "movie_id")
	private MovieEntity movie;
	private LocalDateTime ratedAt;

	public UserRateEntity(String id, UserEntity user, int value, String comment,
			LocalDateTime ratedAt, MovieEntity movie) {
		this.id = UUID.fromString(id);
		this.user = user;
		this.value = value;
		this.comment = comment;
		this.movie = movie;
		this.ratedAt = ratedAt;
	}

	public static UserRateEntity fromDomain(UserRate userRate) {
		return new UserRateEntity(userRate.id(),
				UserEntity.fromDomain(userRate.user()), userRate.rateValue(),
				userRate.comment(), userRate.ratedAt(),
				MovieEntity.fromId(userRate.getMovie().id()));
	}

	public boolean isRatedBy(UserEntity aUser) {
		return this.user.equals(aUser);
	}

	public UserRate toDomain() {
		return new UserRate(this.user.toDomain(), this.value, this.comment,
				this.movie.toDomain());
	}

	// public UserMovieRate toUserMovieRate() {
	// return new UserMovieRate(this.user.userName(), value,
	// new FormattedDateTime(ratedAt).toString(), comment);
	// }
}
