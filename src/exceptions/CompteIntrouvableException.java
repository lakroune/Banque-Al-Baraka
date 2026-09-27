package exceptions;

public class CompteIntrouvableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CompteIntrouvableException(String message) {
        super(message);
    }
}