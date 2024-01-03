package hexagon;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import hexagon.primary.port.DateTimeProvider;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = {"name"})
public class Theater {

	static final String NAME_INVALID = "Theater name cannot be blank";

	private UUID id;
	private String name;
	private DateTimeProvider provider = DateTimeProvider.create();

	private Set<Integer> seatNumbers;

	public Theater(String id, String name, Set<Integer> seats,
			DateTimeProvider provider) {
		this(id, name, seats);
		this.provider = provider;
	}

	public Theater(String id, String name, Set<Integer> seats) {
		this.id = UUID.fromString(id);
		this.name = new NotBlankString(name, NAME_INVALID).value();
		this.seatNumbers = seats;
	}

	public Theater(String name, Set<Integer> seats,
			DateTimeProvider provider) {
		this(UUID.randomUUID().toString(), name, seats,
				provider);
	}

	public Theater(String name, Set<Integer> seats) {
		this(UUID.randomUUID().toString(), name, seats,
				DateTimeProvider.create());
	}

	Set<ShowSeat> seatsForShow(ShowTime show) {
		return this.seatNumbers.stream()
				.map(s -> new ShowSeat(show, s, this.provider))
				.collect(Collectors.toUnmodifiableSet());
	}

	public String name() {
		return name;
	}

	public String id() {
		return id.toString();
	}

	public Set<Integer> seats() {
		return Collections.unmodifiableSet(this.seatNumbers);
	}
}
