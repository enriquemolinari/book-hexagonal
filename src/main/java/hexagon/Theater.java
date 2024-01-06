package hexagon;

import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@EqualsAndHashCode(of = {"name"})
public class Theater {

    static final String NAME_INVALID = "Theater name cannot be blank";

    private final UUID id;
    private final String name;
    private final Set<Integer> seatNumbers;

    public Theater(String id, String name, Set<Integer> seats) {
        this.id = UUID.fromString(id);
        this.name = new NotBlankString(name, NAME_INVALID).value();
        this.seatNumbers = seats;
    }

    public Theater(String name, Set<Integer> seats) {
        this(UUID.randomUUID().toString(), name, seats);
    }

    Set<ShowSeat> seatsForShow(ShowTime show) {
        return this.seatNumbers.stream()
                .map(ShowSeat::new)
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
