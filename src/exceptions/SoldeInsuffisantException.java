package exceptions;

public class SoldeInsuffisantException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SoldeInsuffisantException(String message) {
        super(message);
    }

}
