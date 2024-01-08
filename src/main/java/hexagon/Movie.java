package hexagon;

import hexagon.primary.port.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

//TODO: ver donde poner aquellas clases que usan los adaptadores
public class Movie {

    static final String MOVIE_PLOT_INVALID = "Movie plot must not be null or blank";
    static final String MOVIE_NAME_INVALID = "Movie name must not be null or blank";
    static final String DURATION_INVALID = "Movie's duration must be greater than 0";
    static final String GENRES_INVALID = "You must add at least one genre to the movie";

    private UUID id;

    @Getter
    private String name;
    @Getter
    private int duration;
    @Getter
    private LocalDate releaseDate;
    @Getter
    private String plot;
    private Set<Genre> genres;
    private List<Actor> actors;
    private List<Person> directors;
    private List<UserRate> userRates;
    private Rating rating;
    private List<ShowTime> showTimes;

    public Movie(String id, String name, String plot, int duration,
                 LocalDate releaseDate,
                 Set<Genre> genres, List<Actor> actors, List<Person> directors,
                 int totalUserVotes, float rateValue, float totalValue) {
        this(name, plot, duration, releaseDate, genres, actors,
                directors);
        this.id = UUID.fromString(id);
        this.rating = new Rating(totalUserVotes, rateValue, totalValue);
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
        this.showTimes = new ArrayList<>();
    }

    public Movie(String name, String plot, int duration, LocalDate releaseDate,
                 Set<Genre> genres) {
        this(name, plot, duration, releaseDate, genres, new ArrayList<Actor>(),
                new ArrayList<Person>());
    }

    private void checkGenresAtLeastHasOne(Set<Genre> genres) {
        if (genres.isEmpty()) {
            throw new BusinessException(GENRES_INVALID);
        }
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

    public boolean isNamedLike(String fullOrPartialName) {
        return this.name.contains(fullOrPartialName);
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

    public boolean hasBeenRatedByUserWithId(String userId) {
        return this.userRates.stream().anyMatch(ur -> ur.isRatedBy(userId));
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

    LocalDateTime releaseDateAsDateTime() {
        return this.releaseDate.atTime(0, 0);
    }

    public String id() {
        return this.id.toString();
    }

    public Set<String> genres() {
        return this.genreAsListOfString();
    }

    public Set<Genre> getGenres() {
        return Collections.unmodifiableSet(this.genres);
    }

    public List<UserRate> getUserRates() {
        return new ArrayList<>(this.userRates);
    }

    public int totalUserVotes() {
        return this.rating.totalVotes();
    }

    public float currentRateValue() {
        return this.rating.actualRate();
    }

    public float totalRate() {
        return this.rating.totalRateValue();
    }

    public void showTimes(List<ShowTime> showTimes) {
        this.showTimes = showTimes;
    }

    public void addShowTime(ShowTime show) {
        this.showTimes.add(show);
    }

    public List<Actor> getActors() {
        return Collections.unmodifiableList(this.actors);
    }

    public List<Person> getDirectors() {
        return Collections.unmodifiableList(this.directors);
    }

    public boolean hasShowsBetween(LocalDateTime from, LocalDateTime until) {
        return this.showTimes.stream().anyMatch(s -> s.isStartTimeBetween(from, until));
    }

    public List<ShowTime> showsUntil(LocalDateTime from, LocalDateTime until) {
        return this.showTimes.stream().filter(s -> s.isStartTimeBetween(from, until)).toList();
    }
}
