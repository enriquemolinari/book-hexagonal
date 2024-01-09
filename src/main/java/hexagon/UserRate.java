package hexagon;

import hexagon.primary.port.BusinessException;
import hexagon.primary.port.UserMovieRate;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserRate {

    static final String INVALID_RATING = "Rate value must be an integer value between 0 and 5";
    private UUID id;
    private User user;
    private int value;
    private String comment;
    private Movie movie;
    private LocalDateTime ratedAt;

    public UserRate(String id, User user, int value, String comment,
                    Movie movie) {
        checkValidRateValue(value);
        this.id = UUID.fromString(id);
        this.user = user;
        this.value = value;
        this.comment = comment;
        this.movie = movie;
        this.ratedAt = LocalDateTime.now();
    }

    public UserRate(User user, int value, String comment, Movie movie) {
        this(UUID.randomUUID().toString(), user, value, comment, movie);
    }

    private void checkValidRateValue(int value) {
        if (value < 0 || value > 5) {
            throw new BusinessException(INVALID_RATING);
        }
    }

    public boolean isRatedBy(String userId) {
        return this.user.id().equals(userId);
    }

    public UserMovieRate toUserMovieRate() {
        return new UserMovieRate(this.user.getUserName(), value,
                new FormattedDateTime(ratedAt).toString(), comment);
    }

    public String id() {
        return id.toString();
    }

    public User user() {
        return user;
    }

    public int rateValue() {
        return value;
    }

    public String comment() {
        return comment;
    }

    public Movie getMovie() {
        return movie;
    }

    public LocalDateTime ratedAt() {
        return ratedAt;
    }

}
