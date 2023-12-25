package infra.secondary.jpa.entities;

import java.time.LocalDateTime;
import java.util.Set;

import hexagon.primary.port.DateTimeProvider;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
public class ShowTime {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private LocalDateTime startTime;

	// TODO: esta ok esto aca?
	@Transient
	// When hibernate creates an instance of this class, this will be
	// null if I don't initialize it here.
	private DateTimeProvider timeProvider = DateTimeProvider.create();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_movie")
	private MovieEntity movieToBeScreened;
	private float price;
	@ManyToOne(fetch = FetchType.LAZY)
	private TheaterEntity screenedIn;
	@OneToMany(mappedBy = "show", cascade = CascadeType.PERSIST)
	private Set<ShowSeat> seatsForThisShow;
	@Column(name = "pointsToWin")
	private int pointsThatAUserWin;

	public ShowTime(DateTimeProvider provider, MovieEntity movie,
			LocalDateTime startTime, float price, TheaterEntity screenedIn,
			Set<ShowSeat> showSeats,
			int totalPointsToWin) {
		this.timeProvider = provider;
		this.movieToBeScreened = movie;
		this.price = price;
		this.startTime = startTime;
		this.screenedIn = screenedIn;
		this.seatsForThisShow = showSeats;
		this.pointsThatAUserWin = totalPointsToWin;
	}

	public boolean isStartingAt(LocalDateTime of) {
		return this.startTime.equals(startTime);
	}

	int pointsToEarn() {
		return this.pointsThatAUserWin;
	}

	public boolean hasSeatNumbered(int aSeatNumber) {
		return this.seatsForThisShow.stream()
				.anyMatch(seat -> seat.isSeatNumbered(aSeatNumber));
	}

	String movieName() {
		return this.movieToBeScreened.name();
	}

	String startDateTime() {
		return this.startTime.toString();
	}
}
