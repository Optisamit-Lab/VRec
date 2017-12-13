package uppd.com.vrec.model;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by o.rabinovych on 12/13/17.
 */
@Data
@AllArgsConstructor
public class Recording {
    File file;
    boolean isSent;
}
