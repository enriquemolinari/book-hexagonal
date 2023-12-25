package infra.secondary.jpa.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import hexagon.Actor;
import hexagon.Movie;
import hexagon.Person;
import hexagon.primary.port.Genre;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
public class MovieEntity {

	@Id
	private UUID id;
	private String name;
	private int duration;
	private LocalDate releaseDate;
	private String plot;

	@ElementCollection
	// @OneToMany(cascade = CascadeType.ALL)
	private Set<String> genres;
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_movie")
	private List<ActorEntity> actors;
	@ManyToMany(cascade = CascadeType.ALL)
	private List<DirectorEntity> directors;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "movie")
	// List does not load the entire collection for adding new elements
	// if there is a bidirectional mapping
	private List<UserRate> userRates;

	// this is pre-calculated rating for this movie
	@Embedded
	private Rating rating;

	@OneToMany(mappedBy = "movieToBeScreened")
	private List<ShowTime> showTimes;

	public MovieEntity(String id, String name, String plot, int duration,
			LocalDate releaseDate,
			Set<String> genres) {
		this(id, name, plot, duration, releaseDate, genres,
				new ArrayList<ActorEntity>(), new ArrayList<DirectorEntity>());
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
		this.rating = Rating.notRatedYet();
	}

	// TODO: que hago con esto?
	public UserRate rateBy(User user, int value, String comment) {
		// Ideally validating logic that a user does not rate the same
		// movie twice should be here. However, to do that Hibernate will
		// load the entire collection in memory. That
		// would hurt performance as the collection gets bigger.
		// This validation gets performed in Cimema.
		var userRate = new UserRate(user, value, comment, this);
		this.rating.calculaNewRate(value);
		this.userRates.add(userRate);
		return userRate;
	}

	boolean hasRateValue(float aValue) {
		return this.rating.hasValue(aValue);
	}

	public boolean hasTotalVotes(int votes) {
		return this.rating.hastTotalVotesOf(votes);
	}

	String name() {
		return this.name;
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

	int duration() {
		return this.duration;
	}

	public String id() {
		return this.id.toString();
	}

	LocalDateTime releaseDateAsDateTime() {
		return this.releaseDate.atTime(0, 0);
	}

	List<Actor> toActors() {
		return this.actors.stream().map(a -> a.toDomain())
				.collect(Collectors.toList());
	}

	List<Person> toDirectors() {
		return this.directors.stream().map(d -> d.toDomain())
				.collect(Collectors.toList());
	}

	// TODO: Be very carefull here... full collections? I cannot add proxys
	// full collections or empty, because collections that will grow in the
	// future will be
	// an impact on performance
	public Movie toDomain() {
		return new Movie(this.name, this.plot, this.duration, this.releaseDate,
				genresToDomain(), toActors(), toDirectors());
	}

	private Set<Genre> genresToDomain() {
		return this.genres.stream().map(g -> Genre.valueOf(g.toUpperCase()))
				.collect(Collectors.toUnmodifiableSet());
	}
}
