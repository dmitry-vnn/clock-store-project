package dmitry.clock.logic.exceptions;

public class ClockNotFoundException extends StoreException {

    public ClockNotFoundException() {
        super();
    }

    public ClockNotFoundException(String message) {
        super(message);
    }
}
