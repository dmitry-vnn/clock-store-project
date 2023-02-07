package dmitry.clock.logic.exceptions;

public class EmptyMarkException extends ClockException {
    public EmptyMarkException() {
    }

    public EmptyMarkException(String message) {
        super(message);
    }
}
