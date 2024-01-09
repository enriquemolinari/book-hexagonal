package hexagon.primary.port;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter(value = AccessLevel.PUBLIC)
public class Ticket {
    private final float total;
    private final int pointsWon;
    private final String salesDate;
    private final String username;
    private final List<Integer> payedSeats;
    private final String movieName;
    private final String showStartTime;

    public Ticket(float total, int pointsWon,
                  String formattedSalesDate, String userName,
                  List<Integer> payedSeats, String movieName, String showStartTime) {
        this.total = total;
        this.pointsWon = pointsWon;
        this.salesDate = formattedSalesDate;
        this.username = userName;
        this.payedSeats = payedSeats;
        this.movieName = movieName;
        this.showStartTime = showStartTime;
    }

    public boolean hasSeats(Set<Integer> seats) {
        return this.payedSeats.containsAll(seats);
    }

    public boolean isPurchaserUserName(String aUserName) {
        return this.username.equals(aUserName);
    }

    public float total() {
        return this.total;
    }
}
