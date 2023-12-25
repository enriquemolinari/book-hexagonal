package hexagon.primary.port;

import java.util.List;
import java.util.Set;

public record MovieInfo(String id, String name, String duration, String plot,
		Set<String> genres, List<String> directorNames,
		String releaseDate, String ratingValue, int ratingTotalVotes,
		List<ActorInMovieName> actors) {
}
