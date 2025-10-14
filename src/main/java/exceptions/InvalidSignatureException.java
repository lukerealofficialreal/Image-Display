package exceptions;

public class InvalidSignatureException extends RuntimeException {
    public InvalidSignatureException(String signature) {
        super("Signature '" + signature + "' is not a valid signature.");
    }
}
