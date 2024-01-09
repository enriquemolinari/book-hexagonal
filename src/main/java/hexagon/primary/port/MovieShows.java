package hexagon.primary.port;

import java.util.List;
import java.util.Set;

public record MovieShows(String movieId, String movieName, String duration,
                         Set<String> genres, List<ShowInfo> shows) {
}
