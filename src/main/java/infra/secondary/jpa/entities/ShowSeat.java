package infra.secondary.jpa.entities;

import java.time.LocalDateTime;
import java.util.Set;

import hexagon.primary.port.DateTimeProvider;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
class ShowSeat {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
	private boolean reserved;
	private boolean confirmed;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_show")
	private ShowTime show;
	private LocalDateTime reservedUntil;
	private Integer seatNumber;
	@Version
	private int version;

	@Transient
	private DateTimeProvider provider = DateTimeProvider.create();

	public ShowSeat(ShowTime s, Integer seatNumber,
			DateTimeProvider dateProvider) {
		this.show = s;
		this.seatNumber = seatNumber;

		this.reserved = false;
		this.confirmed = false;
		this.provider = dateProvider;
	}

	public boolean isSeatNumbered(int aSeatNumber) {
		return this.seatNumber.equals(aSeatNumber);
	}

	public boolean isIncludedIn(Set<Integer> selectedSeats) {
		return selectedSeats.stream()
				.anyMatch(ss -> ss.equals(this.seatNumber));
	}

	int seatNumber() {
		return seatNumber;
	}

}
