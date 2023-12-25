package hexagon.secondary.port;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record MovieRequest(String id, String name, String plot, int duration,
		LocalDate releaseDate,
		Set<String> genres, List<ActorRequest> actors,
		List<PersonRequest> directors) {

}
