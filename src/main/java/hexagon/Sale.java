package hexagon;

import hexagon.primary.port.Ticket;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Sale {

    private final UUID id;
    @Getter
    private final float total;
    @Getter
    private final LocalDateTime salesDate;
    @Getter
    private final User purchaser;
    @Getter
    private final int pointsWon;
    @Getter
    private final ShowTime soldShow;
    private final Set<Integer> selectedSeats;

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

    private String formattedSalesDate() {
        return new FormattedDateTime(salesDate).toString();
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
