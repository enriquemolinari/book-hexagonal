package infra.secondary.jpa.entities;

import hexagon.Sale;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "sale")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
public class SaleEntity {
    @Id
    private UUID id;
    private float total;
    private LocalDateTime salesDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    @Setter
    private UserEntity purchaser;
    private int pointsWon;
    @ManyToOne
    @JoinColumn(name = "id_showtime")
    private ShowTimeEntity soldShow;
    private Set<Integer> selectedSeats;

    public static SaleEntity fromDomain(Sale sale) {
        return new SaleEntity(sale.id(), sale.getTotal(),
                ShowTimeEntity.fromId(sale.getSoldShow().id()), sale.getPointsWon(), sale.seats());
    }

    SaleEntity(String id, float totalAmount, ShowTimeEntity soldShow,
               int pointsWon, Set<Integer> selectedSeats) {
        this.id = UUID.fromString(id);
        this.total = totalAmount;
        this.soldShow = soldShow;
        this.selectedSeats = selectedSeats;
        this.salesDate = LocalDateTime.now();
        this.pointsWon = pointsWon;
    }
}
