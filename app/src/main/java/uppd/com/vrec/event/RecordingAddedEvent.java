package uppd.com.vrec.event;

import lombok.Value;
import uppd.com.vrec.model.Recording;

/**
 * Created by o.rabinovych on 12/13/17.
 */
@Value
public class RecordingAddedEvent {
    Recording recording;
}
