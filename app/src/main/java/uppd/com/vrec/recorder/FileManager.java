package uppd.com.vrec.recorder;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import uppd.com.vrec.R;

/**
 * Created by o.rabinovych on 12/4/17.
 */
@Singleton
public class FileManager {
    private static final DateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyMMdd-HHmmss", Locale.US);

    public static final int STORE_TO_INTERNAL = 0;
    public static final int STORE_TO_EXTERNAL = 1;

    private final Context context;

    @Inject
    public FileManager(Context context) {
        this.context = context;
    }

    public File getNewFile() {
        return new File(getRecDir(), FILE_NAME_FORMAT.format(new Date()) + ".aac");
    }

    private File getRecExternalDir() {
        return context.getExternalFilesDir(null);
    }

    public File[] getAllFiles() {
        return getRecDir().listFiles();
    }

    public boolean extStorageAvailable() {
        return Environment.getExternalStorageState(getRecExternalDir()).equals(Environment.MEDIA_MOUNTED);
    }

    public File getRecDir() {
        return storeToExternal() ? getRecInternalDir() : getRecExternalDir();
    }

    private boolean storeToExternal() {
        return extStorageAvailable() && PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.key_storeTo), String.valueOf(STORE_TO_INTERNAL)).equals(String.valueOf(STORE_TO_INTERNAL));
    }

    private File getRecInternalDir() {
        return context.getFilesDir();
    }
}
