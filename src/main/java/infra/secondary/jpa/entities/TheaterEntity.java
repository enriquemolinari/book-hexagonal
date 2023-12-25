package infra.secondary.jpa.entities;

import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"name"})
public class TheaterEntity {

	@Id
	private UUID id;
	@Column(unique = true)
	private String name;
	// @Transient
	// private DateTimeProvider provider = DateTimeProvider.create();

	@ElementCollection(fetch = FetchType.LAZY)
	private Set<Integer> seatNumbers;

	public TheaterEntity(String id, String name, Set<Integer> seats) {
		this.id = UUID.fromString(id);
		this.name = name;
		this.seatNumbers = seats;
		// this.provider = provider;
	}

	// TODO: esto va aca? porque tambien esta en el modelo
	// Set<ShowSeat> seatsForShow(ShowTime show) {
	// return this.seatNumbers.stream()
	// .map(s -> new ShowSeat(show, s, this.provider))
	// .collect(Collectors.toUnmodifiableSet());
	// }

}
