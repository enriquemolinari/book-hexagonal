package spring.main;

import hexagon.primary.port.Genre;
import infra.secondary.jpa.entities.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SetUpDb {

    public static final String SCHOOL_MOVIE_ID = "3c608ba1-1aa5-4f85-9dc6-e0fe4fa4cc0c";
    public static final String SMALL_FISH_MOVIE_ID = "cf48c83d-9444-421a-8726-89fd51e7c843";
    public static final String SHOW_SMAL_FISH_ONE_ID = "acf40a5e-3440-4e5e-b979-b0844aabbb56";

    private final EntityManagerFactory emf;

    public SetUpDb(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void createSchemaAndPopulateSampleData() {
        var em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            var craigDirector = new DirectorEntity(UUID.randomUUID().toString(), "Christopher", "Wegemen",
                    "craig@mymovies.com");
            var judithDirector = new DirectorEntity(UUID.randomUUID().toString(), "Jude", "Zevele",
                    "judith@mymovies.com");
            var andreDirector = new DirectorEntity(UUID.randomUUID().toString(), "Andres", "Lembert",
                    "andre@mymovies.com");
            var colinDirector = new DirectorEntity(UUID.randomUUID().toString(), "Colin", "Clefferd",
                    "andre@mymovies.com");

            var jakeActor = new ActorEntity(UUID.randomUUID().toString(), "Jake", "White", "jake@mymovies.com", "Daniel Finne");
            var jakeActor2 = new ActorEntity(UUID.randomUUID().toString(), "Jake", "White", "jake@mymovies.com", "Camilo Fernis");
            var joshActor = new ActorEntity(UUID.randomUUID().toString(), "Josh", "Blue", "josh@mymovies.com", "Norber Carl");
            var ernestActor = new ActorEntity(UUID.randomUUID().toString(), "Ernest", "Finey", "ernest@mymovies.com", "Edward Blomsky (senior)");
            var nervanActor = new ActorEntity(UUID.randomUUID().toString(), "Nervan", "Allister",
                    "nervan@mymovies.com", "Edward Blomsky (young)");
            var camiloActor = new ActorEntity(UUID.randomUUID().toString(), "Camilo", "Fernandez", "cami@mymovies.com", "Judy");
            var francoActor = new ActorEntity(UUID.randomUUID().toString(), "Franco", "Elchow", "franco@mymovies.com", "George");
            var michaelActor = new ActorEntity(UUID.randomUUID().toString(), "Michael", "Martinez",
                    "michael@mymovies.com", "Mike");
            var michellActor = new ActorEntity(UUID.randomUUID().toString(), "Michael", "Martinez",
                    "michael@mymovies.com", "Teressa");

            var schoolMovie = new MovieEntity(SCHOOL_MOVIE_ID, "Rock in the School",
                    "A teacher tries to teach Rock & Roll music and history "
                            + "to elementary school kids",
                    109, LocalDate.now(), Set.of(Genre.COMEDY.toString(), Genre.ACTION.toString()),
                    List.of(jakeActor, joshActor), List.of(colinDirector));
            var eu = new UserEntity(UUID.randomUUID().toString(), "Enrique", "Molinari",
                    "enrique.molinari@gmail.com", "emolinari", "123456789012");
            em.persist(eu);

            var nu = new UserEntity(UUID.randomUUID().toString(), "Nicolas", "Molimini", "nico@mymovies.com", "nico", "123456789012");
            var lu = new UserEntity(UUID.randomUUID().toString(), "Lucia", "Molimini", "lu@mymovies.com", "lucia", "123456789012");

            em.persist(nu);
            em.persist(lu);

            schoolMovie.addUserRateEntity(List.of(new UserRateEntity(UUID.randomUUID().toString(), eu, 5, "Great Movie", LocalDateTime.now(), schoolMovie)));
            schoolMovie.addUserRateEntity(List.of(new UserRateEntity(UUID.randomUUID().toString(), nu, 5, "Fantastic! The actors, the music, everything is fantastic!", LocalDateTime.now(), schoolMovie)));
            schoolMovie.addUserRateEntity(List.of(new UserRateEntity(UUID.randomUUID().toString(), lu, 4, "I really enjoy the movie", LocalDateTime.now(), schoolMovie)));
            schoolMovie.newRateValues(3, 4.67F, 14);

            em.persist(schoolMovie);

            var fishMovie = new MovieEntity(SMALL_FISH_MOVIE_ID, "Small Fish",
                    "A caring father teaches life values while fishing.", 125,
                    LocalDate.now().minusDays(1),
                    Set.of(Genre.ADVENTURE.toString(), Genre.DRAMA.toString()),
                    List.of(jakeActor2, ernestActor, nervanActor),
                    List.of(andreDirector));

            fishMovie.addUserRateEntity(List.of(new UserRateEntity(UUID.randomUUID().toString(), eu, 4, "Fantastic !!", LocalDateTime.now(), fishMovie)));
            fishMovie.newRateValues(1, 4, 4);

            em.persist(fishMovie);

            var ju = new UserEntity(UUID.randomUUID().toString(), "Josefina", "Simini",
                    "jsimini@mymovies.com", "jsimini", "123456789012");
            em.persist(ju);

            var teaMovie = new MovieEntity(UUID.randomUUID().toString(), "Crash Tea", "A documentary about tea.",
                    105, LocalDate.now().minusDays(3), Set.of(Genre.COMEDY.toString()),
                    List.of(michaelActor, michellActor),
                    List.of(judithDirector, craigDirector));
            em.persist(teaMovie);

            var runningMovie = new MovieEntity(UUID.randomUUID().toString(), "Running far Away",
                    "José a sad person run away from his town looking for new adventures.",
                    105, LocalDate.now(), Set.of(Genre.THRILLER.toString(), Genre.ACTION.toString()),
                    List.of(francoActor, camiloActor), List.of(judithDirector));
            em.persist(runningMovie);

            // Seats from Theatre A
            Set<Integer> seatsA = new HashSet<>();
            Set<ShowSeatEntity> showSeatsAShow1 = new HashSet<>();
            Set<ShowSeatEntity> showSeatsAShow2 = new HashSet<>();
            Set<ShowSeatEntity> showSeatsAShow5 = new HashSet<>();
            for (int i = 1; i <= 30; i++) {
                seatsA.add(i);
                showSeatsAShow1.add(new ShowSeatEntity(UUID.randomUUID().toString(), i, false, false, null));
                showSeatsAShow2.add(new ShowSeatEntity(UUID.randomUUID().toString(), i, false, false, null));
                showSeatsAShow5.add(new ShowSeatEntity(UUID.randomUUID().toString(), i, false, false, null));
            }
            var ta = new TheaterEntity(UUID.randomUUID().toString(), "Theatre A", seatsA);

            em.persist(ta);
            em.flush();

            // Seats from Theatre B
            Set<Integer> seatsB = new HashSet<>();
            Set<ShowSeatEntity> showSeatsBShow3 = new HashSet<>();
            Set<ShowSeatEntity> showSeatsBShow4 = new HashSet<>();
            Set<ShowSeatEntity> showSeatsBShow6 = new HashSet<>();
            for (int i = 1; i <= 50; i++) {
                seatsB.add(i);
                showSeatsBShow3.add(new ShowSeatEntity(UUID.randomUUID().toString(), i, false, false, null));
                showSeatsBShow4.add(new ShowSeatEntity(UUID.randomUUID().toString(), i, false, false, null));
                showSeatsBShow6.add(new ShowSeatEntity(UUID.randomUUID().toString(), i, false, false, null));
            }
            var tb = new TheaterEntity(UUID.randomUUID().toString(), "Theatre B", seatsB);
            em.persist(tb);
            em.flush();

            var show1 = new ShowTimeEntity(SHOW_SMAL_FISH_ONE_ID, fishMovie,
                    LocalDateTime.now().plusDays(1), 10f, ta, 10);
            showSeatsAShow1.forEach(ss -> ss.setShow(show1));
            show1.setSeatsForThisShow(showSeatsAShow1);
            em.persist(show1);

            var show2 = new ShowTimeEntity(UUID.randomUUID().toString(), fishMovie,
                    LocalDateTime.now().plusDays(1).plusHours(4), 10f, ta, 10);
            showSeatsAShow2.forEach(ss -> ss.setShow(show2));
            show2.setSeatsForThisShow(showSeatsAShow2);
            em.persist(show2);

            var show3 = new ShowTimeEntity(UUID.randomUUID().toString(), schoolMovie,
                    LocalDateTime.now().plusDays(2).plusHours(1), 19f, tb, 10);
            showSeatsBShow3.forEach(ss -> ss.setShow(show3));
            show3.setSeatsForThisShow(showSeatsBShow3);
            em.persist(show3);

            var show4 = new ShowTimeEntity(UUID.randomUUID().toString(), schoolMovie,
                    LocalDateTime.now().plusDays(2).plusHours(5), 19f, tb, 10);
            showSeatsBShow4.forEach(ss -> ss.setShow(show4));
            show4.setSeatsForThisShow(showSeatsBShow4);
            em.persist(show4);

            var show5 = new ShowTimeEntity(UUID.randomUUID().toString(), teaMovie,
                    LocalDateTime.now().plusDays(2).plusHours(2), 19f, ta, 10);
            showSeatsAShow5.forEach(ss -> ss.setShow(show5));
            show5.setSeatsForThisShow(showSeatsAShow5);
            em.persist(show5);

            var show6 = new ShowTimeEntity(UUID.randomUUID().toString(), runningMovie,
                    LocalDateTime.now().plusHours(2), 19f, tb, 10);
            showSeatsBShow6.forEach(ss -> ss.setShow(show6));
            show6.setSeatsForThisShow(showSeatsBShow6);
            em.persist(show6);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
