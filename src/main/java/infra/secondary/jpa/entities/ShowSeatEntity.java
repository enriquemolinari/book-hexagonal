package infra.secondary.jpa.entities;

import hexagon.ShowSeat;
import hexagon.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
public class ShowSeatEntity {

    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;
    private boolean reserved;
    private boolean confirmed;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_show")
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
        //TODO: aca showSeat no tiene la instancia de ShowTime
        User user = null;
        if (this.user != null) {
            user = this.user.toDomain();
        }
        return new ShowSeat(this.id.toString(), user, this.seatNumber, this.reserved, this.confirmed, this.reservedUntil);
    }

    void showEntity(ShowTimeEntity showEntity) {
        this.show = showEntity;
    }

    public ShowSeatEntity(ShowTimeEntity s, Integer seatNumber) {
        this.id = UUID.randomUUID();
        this.show = s;
        this.seatNumber = seatNumber;

        this.reserved = false;
        this.confirmed = false;
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

    UserEntity user() {
        return user;
    }

    public void user(UserEntity user) {
        this.user = user;
    }

    public void reserve(LocalDateTime until) {
        this.reserved = true;
        this.reservedUntil = until;
    }

    public void confirm() {
        this.confirmed = true;
    }
}
