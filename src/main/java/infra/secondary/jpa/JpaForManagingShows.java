package infra.secondary.jpa;

import hexagon.Sale;
import hexagon.ShowSeat;
import hexagon.ShowTime;
import hexagon.Theater;
import hexagon.secondary.port.ForManagingShows;
import infra.secondary.jpa.entities.*;
import jakarta.persistence.EntityManager;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class JpaForManagingShows implements ForManagingShows {
    private final EntityManager em;

    public JpaForManagingShows(EntityManager em) {
        this.em = em;
    }

    @Override
    public void addTheater(Theater theater) {
        em.persist(TheaterEntity.fromDomain(theater));
    }

    @Override
    public Optional<Theater> theaterBy(String theaterId) {
        var theaterEntity = em.find(TheaterEntity.class,
                UUID.fromString(theaterId));
        if (theaterEntity != null) {
            return Optional.of(theaterEntity.toDomain());
        }
        return Optional.empty();
    }

    @Override
    public void newShow(ShowTime showTime) {
        em.persist(ShowTimeEntity.fromDomain(showTime));
    }

    @Override
    public Optional<ShowTime> showTimeBy(String showTimeId) {
        var showTimeEntity = em.find(ShowTimeEntity.class, UUID.fromString(showTimeId));
        if (showTimeEntity != null) {
            return Optional.of(showTimeEntity.toDomain(showTimeEntity.movieToBeScreenedToDomain()));
        }
        return Optional.empty();
    }

    @Override
    public void reserve(Set<ShowSeat> reservedSeats) {
        reservedSeats.forEach(reservedSeat -> {
            var showSeatEntity = em.getReference(ShowSeatEntity.class, UUID.fromString(reservedSeat.id()));
            showSeatEntity.setUser(UserEntity.fromId(reservedSeat.user().id()));
            showSeatEntity.reserve(reservedSeat.reservedUntil());
        });
    }

    @Override
    public void confirm(Sale sale, Set<ShowSeat> showSeats) {
        var userEntity = em.getReference(UserEntity.class, UUID.fromString(sale.getPurchaser().id()));
        showSeats.forEach(showSeat -> {
            var showSeatEntity = em.getReference(ShowSeatEntity.class, UUID.fromString(showSeat.id()));
            showSeatEntity.setUser(UserEntity.fromId(showSeat.user().id()));
            showSeatEntity.confirm();
        });
        var saleEntity = SaleEntity.fromDomain(sale);
        userEntity.setPoints(sale.getPurchaser().getPoints());
        userEntity.addPurchase(saleEntity);
    }
}
