package hexagon.secondary.port;

import hexagon.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ForManagingShows {
    void addTheater(Theater theater);

    Theater theaterBy(String theaterId);

    void newShow(ShowTime showTime);

    List<Movie> showsUntil(LocalDateTime untilTo);

    ShowTime showTimeBy(String showTimeId);

    void reserve(Set<ShowSeat> reservedSeats);

    void confirm(Sale sale, Set<ShowSeat> showSeats);
}
