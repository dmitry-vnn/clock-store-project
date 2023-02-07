package dmitry.clock.logic.exceptions;

public class EmptyStoreException extends StoreException {

    public EmptyStoreException() {
        super();
    }

    public EmptyStoreException(String message) {
        super(message);
    }
}
