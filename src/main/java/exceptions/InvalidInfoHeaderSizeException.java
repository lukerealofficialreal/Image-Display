package exceptions;

public class InvalidInfoHeaderSizeException extends RuntimeException {
    public InvalidInfoHeaderSizeException(int size) {
        super("Size '" + size + "' was not the expected info header size.");
    }
}
