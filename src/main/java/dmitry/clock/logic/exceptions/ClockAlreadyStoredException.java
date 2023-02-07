package dmitry.clock.logic.exceptions;

public class ClockAlreadyStoredException extends ClockException {

    public ClockAlreadyStoredException() {
        super();
    }

    public ClockAlreadyStoredException(String message) {
        super(message);
    }
}
