package hexagon.primary.port;

import java.util.List;

public record DetailedShowInfo(ShowInfo info, String theater,
		List<Seat> currentSeats) {

}
