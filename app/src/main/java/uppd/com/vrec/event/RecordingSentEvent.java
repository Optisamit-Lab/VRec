package uppd.com.vrec.event;

import java.io.File;

import lombok.Value;

/**
 * Created by o.rabinovych on 12/13/17.
 */
@Value
public class RecordingSentEvent {
    File file;
}
