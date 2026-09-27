package exceptions;

public class ClientIntrouvableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ClientIntrouvableException(String message) {
        super(message);
    }
}