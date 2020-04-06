package clightning;

/**
 * Exception thrown while there is an error executing the lightning daemon command
 */
public class RemoteException extends RuntimeException {
    private int code;

    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, int code) {
        super(message);
        this.code = code;
    }

    public RemoteException(Throwable cause) {
        super(cause);
    }
}
