package app.handler;

public class InvalidUsernameException extends RuntimeException {
  public InvalidUsernameException(String message) {
    super(message);
  }
}
