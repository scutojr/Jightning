package clightning;

public class RemoteException extends RuntimeException {
    private int code;

    public RemoteException(String message, int code) {
        super(message);
        this.code = code;
    }

    public RemoteException(Throwable cause) {
        super(cause);
    }
}
