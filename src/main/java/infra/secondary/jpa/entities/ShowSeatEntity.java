package infra.secondary.jpa.entities;

import hexagon.ShowSeat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
public class ShowSeatEntity {

    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private UserEntity user;
    private boolean reserved;
    private boolean confirmed;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_show")
    @Setter
    private ShowTimeEntity show;
    private LocalDateTime reservedUntil;
    private Integer seatNumber;
    @Version
    private int version;

    public ShowSeatEntity(String id, Integer seatNumber, boolean reserved, boolean confirmed, LocalDateTime reservedUntil) {
        this.id = UUID.fromString(id);
        this.seatNumber = seatNumber;
        this.reserved = reserved;
        this.confirmed = confirmed;
        this.reservedUntil = reservedUntil;
    }

    ShowSeat toDomain() {
        return new ShowSeat(this.id.toString(), this.user != null ? this.user.toDomain() : null,
                this.seatNumber, this.reserved, this.confirmed, this.reservedUntil);
    }

    public void reserve(LocalDateTime until) {
        this.reserved = true;
        this.reservedUntil = until;
    }

    public void confirm() {
        this.confirmed = true;
    }
}
