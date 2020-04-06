package clightning;

/**
 * This exception will be thrown if the jason format data is failed to convert
 * to object of specific type.
 */
public class JsonFormatException extends RuntimeException {
    public JsonFormatException(Throwable cause) {
        super(cause);
    }
}
