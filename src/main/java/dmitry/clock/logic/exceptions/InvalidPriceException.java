package dmitry.clock.logic.exceptions;

public class InvalidPriceException extends ClockException {

    public InvalidPriceException() {
        super();
    }

    public InvalidPriceException(String message) {
        super(message);
    }
}
