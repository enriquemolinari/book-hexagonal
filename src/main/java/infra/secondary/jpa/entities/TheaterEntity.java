package infra.secondary.jpa.entities;

import hexagon.Theater;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

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

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<Integer> seatNumbers;

    TheaterEntity(String id) {
        this.id = UUID.fromString(id);
    }

    public TheaterEntity(String id, String name, Set<Integer> seats) {
        this.id = UUID.fromString(id);
        this.name = name;
        this.seatNumbers = seats;
    }

    public Theater toDomain() {
        return new Theater(this.id.toString(), name, seatNumbers);
    }

    public static TheaterEntity fromId(String id) {
        return new TheaterEntity(id);
    }

    //TODO: eliminar
/*	Set<ShowSeatEntity> seatsForShow(ShowTimeEntity show) {
		return this.seatNumbers.stream()
				.map(s -> new ShowSeatEntity(show, s))
				.collect(Collectors.toUnmodifiableSet());
	}*/

}
