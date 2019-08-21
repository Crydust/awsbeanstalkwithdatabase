package be.crydust.spike.business.users;

public class WebApplicationException extends RuntimeException {
    public WebApplicationException() {
    }

    public WebApplicationException(String message) {
        super(message);
    }

    public WebApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebApplicationException(Throwable cause) {
        super(cause);
    }
}
