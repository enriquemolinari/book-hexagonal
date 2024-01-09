package hexagon;

import hexagon.primary.port.BusinessException;
import hexagon.primary.port.Seat;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class ShowSeat {
    static final String SEAT_BUSY = "Seat is currently busy";
    static final String SEAT_NOT_RESERVED_OR_ALREADY_CONFIRMED = "The seat cannot be confirmed";

    private final UUID id;
    private User user;
    private boolean reserved;
    private boolean confirmed;
    private LocalDateTime reservedUntil;
    private final Integer seatNumber;

    public ShowSeat(String id, User user, Integer seatNumber, boolean reserved, boolean confirmed, LocalDateTime reservedUntil) {
        this.id = UUID.fromString(id);
        this.seatNumber = seatNumber;
        this.user = user;
        this.reserved = reserved;
        this.confirmed = confirmed;
        this.reservedUntil = reservedUntil;
    }

    public ShowSeat(Integer seatNumber) {
        this.id = UUID.randomUUID();
        this.seatNumber = seatNumber;
        this.reserved = false;
        this.confirmed = false;
    }

    public void doReserveForUser(User user, LocalDateTime until) {
        if (!isAvailable()) {
            throw new BusinessException(SEAT_BUSY);
        }
        this.reserved = true;
        this.user = user;
        this.reservedUntil = until;
    }

    public boolean isBusy() {
        return !isAvailable();
    }

    public boolean isAvailable() {
        return (!reserved || LocalDateTime.now().isAfter(this.reservedUntil))
                && !confirmed;
    }

    public void doConfirmForUser(User user) {
        if (!isReservedBy(user) || confirmed) {
            throw new BusinessException(SEAT_NOT_RESERVED_OR_ALREADY_CONFIRMED);
        }
        this.confirmed = true;
        this.user = user;
    }

    boolean isConfirmedBy(User user) {
        if (this.user == null) {
            return false;
        }
        return confirmed && this.user.equals(user);
    }

    boolean isReservedBy(User user) {
        if (this.user == null) {
            return false;
        }
        return reserved && this.user.equals(user)
                && LocalDateTime.now().isBefore(this.reservedUntil);
    }

    public boolean isSeatNumbered(int aSeatNumber) {
        return this.seatNumber.equals(aSeatNumber);
    }

    public boolean isIncludedIn(Set<Integer> selectedSeats) {
        return selectedSeats.stream()
                .anyMatch(ss -> ss.equals(this.seatNumber));
    }

    public LocalDateTime reservedUntil() {
        return this.reservedUntil;
    }

    public int seatNumber() {
        return seatNumber;
    }

    public String id() {
        return this.id.toString();
    }

    public boolean reserved() {
        return reserved;
    }

    public boolean confirmed() {
        return confirmed;
    }

    public Seat toSeat() {
        return new Seat(seatNumber, isAvailable());
    }

    public User user() {
        return this.user;
    }

    public boolean hasBeenReserveOrConfirm() {
        return this.user != null;
    }
}
