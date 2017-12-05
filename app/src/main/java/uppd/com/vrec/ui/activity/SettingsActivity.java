package uppd.com.vrec.ui.activity;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import uppd.com.vrec.ui.fragment.SettingsFragment;

/**
 * Created by o.rabinovych on 12/5/17.
 */

public class SettingsActivity extends SingleFragmentActivity {
    @NonNull
    @Override
    protected Fragment getContentFragment() {
        return new SettingsFragment();
    }
}
