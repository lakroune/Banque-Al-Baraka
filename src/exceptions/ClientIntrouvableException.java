package exceptions;

public class ClientIntrouvableException extends RuntimeException {
    public ClientIntrouvableException(String message) {
        super(message);
    }
}