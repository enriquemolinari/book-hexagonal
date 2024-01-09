package infra.secondary.jpa.entities;

import hexagon.Actor;
import hexagon.Movie;
import hexagon.Person;
import hexagon.primary.port.Genre;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "movie")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
public class MovieEntity {

    @Id
    private UUID id;
    @Getter
    private String name;
    private int duration;
    private LocalDate releaseDate;
    private String plot;

    @ElementCollection
    private Set<String> genres;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_movie")
    private List<ActorEntity> actors;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<DirectorEntity> directors;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "movie")
    // List does not load the entire collection for adding new elements
    // if there is a bidirectional mapping
    private List<UserRateEntity> userRates;
    private int totalUserVotes;
    private float rateValue;
    private float totalValue;
    @OneToMany(mappedBy = "movieToBeScreened")
    private List<ShowTimeEntity> showTimes;

    public static MovieEntity fromId(String id) {
        return new MovieEntity(id);
    }

    public static MovieEntity fromDomain(Movie movie) {
        return new MovieEntity(movie.id(), movie.getName(),
                movie.getPlot(),
                movie.getDuration(), movie.getReleaseDate(),
                movie.genres());
    }

    MovieEntity(String id) {
        this.id = UUID.fromString(id);
    }

    MovieEntity(String id, String name, String plot, int duration,
                LocalDate releaseDate,
                Set<String> genres) {
        this(id, name, plot, duration, releaseDate, genres,
                new ArrayList<>(), new ArrayList<>());
    }

    public MovieEntity(String id, String name, String plot, int duration,
                       LocalDate releaseDate,
                       Set<String> genres, List<ActorEntity> actors,
                       List<DirectorEntity> directors) {
        this.id = UUID.fromString(id);
        this.name = name;
        this.plot = plot;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.genres = genres;
        this.actors = actors;
        this.directors = directors;
        this.userRates = new ArrayList<>();
    }

    public void addAnActor(String id, String name, String surname, String email,
                           String characterName) {
        this.actors.add(
                new ActorEntity(id, name, surname, email, characterName));
    }

    public void addADirector(String id, String name, String surname,
                             String email) {
        this.directors.add(new DirectorEntity(id, name, surname, email));
    }

    public String id() {
        return this.id.toString();
    }

    List<Actor> toActors() {
        return this.actors.stream().map(ActorEntity::toDomain)
                .collect(Collectors.toList());
    }

    List<Person> toDirectors() {
        return this.directors.stream().map(DirectorEntity::toDomain)
                .collect(Collectors.toList());
    }

    public Movie toDomain() {
        var movie = new Movie(this.id(), this.name, this.plot,
                this.duration,
                this.releaseDate,
                genresToDomain(), toActors(), toDirectors(),
                this.totalUserVotes, this.rateValue, this.totalValue);
        var showTimes = this.showTimes.stream().map(st -> st.toDomain(movie)).collect(Collectors.toList());
        movie.showTimes(showTimes);
        return movie;
    }

    private Set<Genre> genresToDomain() {
        return this.genres.stream().map(g -> Genre.valueOf(g.toUpperCase()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public void newRateValues(int totalUserVotes, float newRateValue,
                              float totalValue) {
        this.totalUserVotes = totalUserVotes;
        this.rateValue = newRateValue;
        this.totalValue = totalValue;
    }

    public void addUserRateEntity(List<UserRateEntity> userRateEntityList) {
        this.userRates.addAll(userRateEntityList);
    }
}
