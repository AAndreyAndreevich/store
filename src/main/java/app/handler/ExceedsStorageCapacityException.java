package app.handler;

public class ExceedsStorageCapacityException extends RuntimeException {
    public ExceedsStorageCapacityException(String message) {
        super(message);
    }
}
