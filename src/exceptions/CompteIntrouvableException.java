package exceptions;

public class CompteIntrouvableException extends RuntimeException {
    public CompteIntrouvableException(String message) {
        super(message);
    }
}