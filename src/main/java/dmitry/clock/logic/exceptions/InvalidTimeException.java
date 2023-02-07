package dmitry.clock.logic.exceptions;

public class InvalidTimeException extends ClockException {

    public InvalidTimeException() {
        super();
    }

    public InvalidTimeException(String message) {
        super(message);
    }
}
