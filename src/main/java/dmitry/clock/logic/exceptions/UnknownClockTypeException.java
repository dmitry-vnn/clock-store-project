package dmitry.clock.logic.exceptions;

public class UnknownClockTypeException extends ClockException {

    public UnknownClockTypeException() {
        super();
    }

    public UnknownClockTypeException(String message) {
        super(message);
    }
}
