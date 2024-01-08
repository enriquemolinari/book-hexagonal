package infra.secondary.jpa.entities;

import hexagon.Theater;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "theater")
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

    public static TheaterEntity fromDomain(Theater theater) {
        return new TheaterEntity(theater.id(), theater.name(),
                theater.seats());
    }

    public TheaterEntity(String id, String name, Set<Integer> seats) {
        this.id = UUID.fromString(id);
        this.name = name;
        this.seatNumbers = seats;
    }

    public Theater toDomain() {
        return new Theater(this.id.toString(), name, seatNumbers);
    }
}
