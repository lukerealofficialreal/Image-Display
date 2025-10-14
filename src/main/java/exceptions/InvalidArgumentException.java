package exceptions;

public class InvalidArgumentException extends RuntimeException {
    public InvalidArgumentException(String badArgument) {
        super("Argument '" + badArgument + "' is not supported.");
    }

    public InvalidArgumentException(String badArgument, String message) {
        super("Argument '" + badArgument + "' is not supported: " + message);
    }
}
