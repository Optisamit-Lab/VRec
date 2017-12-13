package uppd.com.vrec.recorder;

import android.content.Context;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by o.rabinovych on 12/4/17.
 */
@Singleton
public class FileManager {
    private static final DateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyMMdd-HHmmss", Locale.US);
    private final Context context;

    @Inject
    public FileManager(Context context){
        this.context = context;
    }

    public File getNewFile() {
        return new File(getRecDir(), FILE_NAME_FORMAT.format(new Date()) + ".aac");
    }

    private File getRecDir() {
        return context.getExternalFilesDir(null);
    }

    public File[] getAllFiles() {
        return getRecDir().listFiles();
    }
}
