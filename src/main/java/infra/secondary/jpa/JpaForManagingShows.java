package infra.secondary.jpa;

import hexagon.*;
import hexagon.secondary.port.ForManagingShows;
import infra.secondary.jpa.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class JpaForManagingShows implements ForManagingShows {
    private EntityManager em;

    public JpaForManagingShows(EntityManager em) {
        this.em = em;
    }

    @Override
    public void addTheater(Theater theater) {
        var t = new TheaterEntity(theater.id(), theater.name(),
                theater.seats());
        em.persist(t);
    }

    @Override
    public Theater theaterBy(String theaterId) {
        var theaterEntity = em.find(TheaterEntity.class,
                UUID.fromString(theaterId));
        return theaterEntity.toDomain();
    }

    @Override
    public void newShow(ShowTime showTime) {
        var st = ShowTimeEntity.fromDomain(showTime);
        em.persist(st);
    }

    @Override
    public List<Movie> showsUntil(LocalDateTime untilTo) {
        var q = em.createQuery(
                        "from MovieEntity m "
                                + "join fetch m.showTimes s join fetch s.screenedIn "
                                + "where s.startTime >= ?1 and s.startTime <= ?2 "
                                + "order by m.name asc",
                        MovieEntity.class).setParameter(1, LocalDateTime.now())
                .setParameter(2, untilTo);
        return movieEntitiesToDomain(q);
    }

    @Override
    public ShowTime showTimeBy(String showTimeId) {
        var ste = em.getReference(ShowTimeEntity.class, UUID.fromString(showTimeId));
        return ste.toDomain(ste.movie().toDomain());
    }

    @Override
    public void reserve(Set<ShowSeat> reservedSeats) {
        reservedSeats.forEach(rs -> {
            var sse = em.getReference(ShowSeatEntity.class, UUID.fromString(rs.id()));
            sse.user(UserEntity.fromId(rs.user().id()));
            sse.reserve(rs.reservedUntil());
        });
    }

    @Override
    public void confirm(Sale sale, Set<ShowSeat> showSeats) {
        var userEntity = em.getReference(UserEntity.class, UUID.fromString(sale.getPurchaser().id()));
        showSeats.forEach(cs -> {
            var sse = em.getReference(ShowSeatEntity.class, UUID.fromString(cs.id()));
            sse.user(UserEntity.fromId(cs.user().id()));
            sse.confirm();
        });
        SaleEntity.fromDomain(sale).purchasedBy(userEntity);
    }

    // TODO: sacar duplicado
    private List<Movie> movieEntitiesToDomain(TypedQuery<MovieEntity> q) {
        return q.getResultList().stream().map(me -> me.toDomain()).toList();
    }
}
