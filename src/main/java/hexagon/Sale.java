package hexagon;

import hexagon.primary.port.Ticket;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Sale {

    private UUID id;
    @Getter
    private float total;
    @Getter
    private LocalDateTime salesDate;
    @Getter
    private User purchaser;
    @Getter
    private int pointsWon;
    @Getter
    private ShowTime soldShow;
    private Set<Integer> selectedSeats;

    public Sale(float totalAmount, User userThatPurchased, ShowTime soldShow,
                int pointsWon, Set<Integer> selectedSeats) {
        this.id = UUID.randomUUID();
        this.total = totalAmount;
        this.purchaser = userThatPurchased;
        this.soldShow = soldShow;
        this.selectedSeats = selectedSeats;
        this.salesDate = LocalDateTime.now();
        this.pointsWon = pointsWon;
        userThatPurchased.newPurchase(this, pointsWon);
    }

    public boolean hasTotalOf(float aTotal) {
        return this.total == aTotal;
    }

    private String formattedSalesDate() {
        return new FormattedDateTime(salesDate).toString();
    }

    boolean purchaseBy(User aUser) {
        return this.purchaser.equals(aUser);
    }

    List<Integer> confirmedSeatNumbers() {
        return this.selectedSeats.stream().toList();
    }

    public Ticket ticket() {
        return new Ticket(total, pointsWon, formattedSalesDate(),
                purchaser.getUserName(), confirmedSeatNumbers(),
                soldShow.movieName(), soldShow.startDateTime());
    }

    public String id() {
        return this.id.toString();
    }

    public Set<Integer> seats() {
        return Collections.unmodifiableSet(this.selectedSeats);
    }
}
