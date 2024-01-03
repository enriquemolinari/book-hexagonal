package infra.secondary.jpa.entities;

import hexagon.Movie;
import hexagon.ShowSeat;
import hexagon.ShowTime;
import hexagon.primary.port.DateTimeProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
public class ShowTimeEntity {

    @Id
    private UUID id;
    private LocalDateTime startTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_movie")
    private MovieEntity movieToBeScreened;
    private float price;
    @ManyToOne(fetch = FetchType.LAZY)
    private TheaterEntity screenedIn;
    @OneToMany(mappedBy = "show", cascade = CascadeType.PERSIST)
    private Set<ShowSeatEntity> seatsForThisShow;
    @Column(name = "pointsToWin")
    private int pointsThatAUserWin;

    public static ShowTimeEntity fromId(String id) {
        return new ShowTimeEntity(UUID.fromString(id));
    }

    ShowTimeEntity(UUID id) {
        this.id = id;
    }

    public ShowTimeEntity(String id, MovieEntity movie, LocalDateTime startTime,
                          float price, TheaterEntity screenedIn, Set<ShowSeatEntity> seats,
                          int totalPointsToWin) {

        this.id = UUID.fromString(id);
        this.movieToBeScreened = movie;
        this.price = price;
        this.startTime = startTime;
        this.screenedIn = screenedIn;
        this.seatsForThisShow = seats;
        this.pointsThatAUserWin = totalPointsToWin;
    }

    public static ShowTimeEntity fromDomain(ShowTime showTime) {
        var ss = showTime.seats();
        var ste = new ShowTimeEntity(showTime.id(),
                MovieEntity.fromId(showTime.movieScreened().id()),
                showTime.startTime(), showTime.price(),
                new TheaterEntity(showTime.screenedIn().id(),
                        showTime.screenedIn().name(),
                        showTime.screenedIn().seats()), null, //TODO: sacar ese null
                showTime.point());
        var showSeatEntities = ss.stream().map(s -> showSeatToEntity(s, ste)).collect(Collectors.toSet());
        ste.setSeatsForThisShow(showSeatEntities);
        return ste;
    }

    private static ShowSeatEntity showSeatToEntity(ShowSeat s, ShowTimeEntity ste) {
        var sse = new ShowSeatEntity(s.id(), s.seatNumber(), s.reserved(), s.confirmed(), s.reservedUntil());
        sse.showEntity(ste);
        if (s.user() != null) {
            sse.user(UserEntity.fromDomain(s.user()));
        }
        return sse;
    }

    public boolean isStartingAt(LocalDateTime of) {
        return this.startTime.equals(startTime);
    }

    int pointsToEarn() {
        return this.pointsThatAUserWin;
    }

    public boolean hasSeatNumbered(int aSeatNumber) {
        return this.seatsForThisShow.stream()
                .anyMatch(seat -> seat.isSeatNumbered(aSeatNumber));
    }

    String movieName() {
        return this.movieToBeScreened.name();
    }

    String startDateTime() {
        return this.startTime.toString();
    }

    public ShowTime toDomain(Movie movie) {
        var ss = this.seatsForThisShow.stream().map(sse -> sse.toDomain()).collect(Collectors.toSet());
        return new ShowTime(this.id.toString(), DateTimeProvider.create(),
                movie, this.startTime,
                this.price, this.screenedIn.toDomain(), this.pointsThatAUserWin, ss);
    }

    public MovieEntity movie() {
        return this.movieToBeScreened;
    }
}
