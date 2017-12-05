package uppd.com.vrec.exception;

/**
 * Created by o.rabinovych on 12/4/17.
 */

public class RecordingException extends RuntimeException {
    public RecordingException(String msg) {
        super(msg);
    }

    public RecordingException(Throwable cause) {
        super(cause);
    }
}
