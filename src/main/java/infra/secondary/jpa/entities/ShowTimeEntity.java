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
@Table(name = "showtime")
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
    @Setter
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
                          float price, TheaterEntity screenedIn, int totalPointsToWin) {
        this.id = UUID.fromString(id);
        this.movieToBeScreened = movie;
        this.price = price;
        this.startTime = startTime;
        this.screenedIn = screenedIn;
        this.pointsThatAUserWin = totalPointsToWin;
    }

    public static ShowTimeEntity fromDomain(ShowTime showTime) {
        var showSeats = showTime.seats();
        var showTimeEntity = new ShowTimeEntity(showTime.id(),
                MovieEntity.fromId(showTime.movieScreened().id()),
                showTime.startTime(), showTime.price(),
                new TheaterEntity(showTime.screenedIn().id(),
                        showTime.screenedIn().name(),
                        showTime.screenedIn().seats()), showTime.pointsToEarn());
        var showSeatEntities = showSeats.stream()
                .map(showSeat -> showSeatToEntity(showSeat, showTimeEntity)).collect(Collectors.toSet());
        showTimeEntity.setSeatsForThisShow(showSeatEntities);
        return showTimeEntity;
    }

    private static ShowSeatEntity showSeatToEntity(ShowSeat showSeat, ShowTimeEntity showTimeEntity) {
        var showSeatEntity = new ShowSeatEntity(showSeat.id(), showSeat.seatNumber(), showSeat.reserved(),
                showSeat.confirmed(), showSeat.reservedUntil());
        showSeatEntity.setShow(showTimeEntity);
        if (showSeat.hasBeenReserveOrConfirm()) {
            showSeatEntity.setUser(UserEntity.fromDomain(showSeat.user()));
        }
        return showSeatEntity;
    }

    public ShowTime toDomain(Movie movie) {
        var ss = this.seatsForThisShow.stream().map(ShowSeatEntity::toDomain).collect(Collectors.toSet());
        return new ShowTime(this.id.toString(), DateTimeProvider.create(),
                movie, this.startTime,
                this.price, this.screenedIn.toDomain(), this.pointsThatAUserWin, ss);
    }

    public Movie movieToBeScreenedToDomain() {
        return this.movieToBeScreened.toDomain();
    }
}
