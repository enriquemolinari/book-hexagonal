package hexagon.secondary.port;

import hexagon.Sale;
import hexagon.ShowSeat;
import hexagon.ShowTime;
import hexagon.Theater;

import java.util.Optional;
import java.util.Set;

public interface ForManagingShows {
    void addTheater(Theater theater);

    Optional<Theater> theaterBy(String theaterId);

    void newShow(ShowTime showTime);

    Optional<ShowTime> showTimeBy(String showTimeId);

    void reserve(Set<ShowSeat> reservedSeats);

    void confirm(Sale sale, Set<ShowSeat> showSeats);
}
