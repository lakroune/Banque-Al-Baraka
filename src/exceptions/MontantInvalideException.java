package exceptions;

public class MontantInvalideException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MontantInvalideException(String message) {
        super(message);
    }
}