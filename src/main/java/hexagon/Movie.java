package hexagon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import hexagon.primary.port.ActorInMovieName;
import hexagon.primary.port.BusinessException;
import hexagon.primary.port.Genre;
import hexagon.primary.port.MovieInfo;
import hexagon.primary.port.MovieShows;

//TODO: ver donde poner aquellas clases que usan los adaptadores
public class Movie {

	static final String MOVIE_PLOT_INVALID = "Movie plot must not be null or blank";
	static final String MOVIE_NAME_INVALID = "Movie name must not be null or blank";
	static final String DURATION_INVALID = "Movie's duration must be greater than 0";
	static final String GENRES_INVALID = "You must add at least one genre to the movie";

	private UUID id;
	private String name;
	private int duration;
	private LocalDate releaseDate;
	private String plot;
	private Set<Genre> genres;
	private List<Actor> actors;
	private List<Person> directors;
	private List<UserRate> userRates;
	private Rating rating;
	private List<ShowTime> showTimes;

	Movie(String id, String name, String plot, int duration,
			LocalDate releaseDate,
			Set<Genre> genres, List<Actor> actors, List<Person> directors) {
		this(name, plot, duration, releaseDate, genres, actors,
				directors);
		this.id = UUID.fromString(id);
	}

	public Movie(String name, String plot, int duration, LocalDate releaseDate,
			Set<Genre> genres, List<Actor> actors, List<Person> directors) {
		checkDurationGreaterThanZero(duration);
		checkGenresAtLeastHasOne(genres);
		this.id = UUID.randomUUID();
		this.name = new NotBlankString(name, MOVIE_NAME_INVALID).value();
		this.plot = new NotBlankString(plot, MOVIE_PLOT_INVALID).value();
		this.duration = duration;
		this.releaseDate = releaseDate;
		this.genres = genres;
		this.actors = actors;
		this.directors = directors;
		this.userRates = new ArrayList<>();
		this.rating = Rating.notRatedYet();
	}

	public Movie(String name, String plot, int duration, LocalDate releaseDate,
			Set<Genre> genres) {
		this(name, plot, duration, releaseDate, genres, new ArrayList<Actor>(),
				new ArrayList<Person>());
	}

	private <T> void checkCollectionSize(Set<T> collection, String errorMsg) {
		if (collection.size() == 0) {
			throw new BusinessException(errorMsg);
		}
	}

	private void checkGenresAtLeastHasOne(Set<Genre> genres) {
		checkCollectionSize(genres, GENRES_INVALID);
	}

	private void checkDurationGreaterThanZero(int duration) {
		if (duration <= 0) {
			throw new BusinessException(DURATION_INVALID);
		}
	}

	public boolean hasDurationOf(int aDuration) {
		return this.duration == aDuration;
	}

	public boolean isNamed(String aName) {
		return this.name.equals(aName);
	}

	public boolean isNamedAs(Movie aMovie) {
		return this.name.equals(aMovie.name);
	}

	public boolean hasReleaseDateOf(LocalDate aDate) {
		return releaseDate.equals(aDate);
	}

	static Set<Genre> genresFrom(Set<String> genres) {
		return genres.stream().map(g -> Genre.valueOf(g))
				.collect(Collectors.toUnmodifiableSet());
	}

	public boolean hasGenresOf(List<Genre> genddres) {
		return this.genres.stream().allMatch(g -> genddres.contains(g));
	}

	public boolean hasARole(String anActorName) {
		return this.actors.stream().anyMatch(a -> a.isNamed(anActorName));
	}

	public boolean isCharacterNamed(String aCharacterName) {
		return this.actors.stream()
				.anyMatch(a -> a.hasCharacterName(aCharacterName));
	}

	public boolean isDirectedBy(String aDirectorName) {
		return this.directors.stream().anyMatch(d -> d.isNamed(aDirectorName));
	}

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

	public String name() {
		return this.name;
	}

	public MovieShows toMovieShow() {
		return new MovieShows(this.id.toString(), this.name,
				new MovieDurationFormat(duration).toString(),
				genreAsListOfString(), this.showTimes.stream()
						.map(show -> show.toShowInfo()).toList());
	}

	public Actor addAnActor(String name, String surname, String email,
			String characterName) {
		var actor = new Actor(new Person(name, surname, email), characterName);
		this.actors.add(actor);
		return actor;
	}

	public Person addADirector(String name, String surname, String email) {
		var director = new Person(name, surname, email);
		this.directors.add(director);
		return director;
	}

	public MovieInfo toInfo() {
		return new MovieInfo(id.toString(), name,
				new MovieDurationFormat(duration).toString(), plot,
				genreAsListOfString(), directorsNamesAsString(),
				new FormattedDate(releaseDate).toString(),
				rating.actualRateAsString(), rating.totalVotes(),
				toActorsInMovieNames());
	}

	private List<String> directorsNamesAsString() {
		return directors.stream().map(d -> d.fullName()).toList();
	}

	private List<ActorInMovieName> toActorsInMovieNames() {
		return this.actors.stream()
				.map(actor -> new ActorInMovieName(actor.fullName(),
						actor.characterName()))
				.toList();
	}

	private Set<String> genreAsListOfString() {
		return this.genres.stream().map(g -> capitalizeFirstLetter(g.name()))
				.collect(Collectors.toSet());
	}

	private String capitalizeFirstLetter(String aString) {
		return aString.substring(0, 1).toUpperCase()
				+ aString.substring(1).toLowerCase();
	}

	public int duration() {
		return this.duration;
	}

	LocalDateTime releaseDateAsDateTime() {
		return this.releaseDate.atTime(0, 0);
	}

	public String id() {
		return this.id.toString();
	}

	public String plot() {
		return this.plot;
	}

	public LocalDate releaseDate() {
		return this.releaseDate;
	}

	public Set<String> genres() {
		return this.genreAsListOfString();
	}

}
