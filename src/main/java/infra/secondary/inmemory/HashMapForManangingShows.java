package infra.secondary.inmemory;

import hexagon.Sale;
import hexagon.ShowSeat;
import hexagon.ShowTime;
import hexagon.Theater;
import hexagon.secondary.port.ForManagingShows;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class HashMapForManangingShows implements ForManagingShows {
    private final Map<String, Theater> theaters;
    private final Map<String, ShowTime> shows;

    public HashMapForManangingShows() {
        this.theaters = new HashMap<>();
        this.shows = new HashMap<>();
    }

    @Override
    public void addTheater(Theater theater) {
        this.theaters.put(theater.id(), theater);
    }

    @Override
    public Optional<Theater> theaterBy(String theaterId) {
        return Optional.ofNullable(this.theaters.get(theaterId));
    }

    @Override
    public void newShow(ShowTime showTime) {
        this.shows.put(showTime.id(), showTime);
    }

    @Override
    public Optional<ShowTime> showTimeBy(String showTimeId) {
        return Optional.ofNullable(this.shows.get(showTimeId));
    }

    @Override
    public void reserve(Set<ShowSeat> reservedSeats) {
        //not required to do nothing else
    }

    @Override
    public void confirm(Sale sale, Set<ShowSeat> showSeats) {
        //not required to do nothing else
    }
}
