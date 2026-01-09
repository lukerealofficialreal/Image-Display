package exceptions;

public class InvalidArgumentValueException extends RuntimeException {

    public InvalidArgumentValueException(String value) {
        super("Value '" + value + "' not valid for parameter.");
    }
    public InvalidArgumentValueException(String value, String message) {
        super("Value '" + value + "' not valid for parameter: " + message);
    }

}
