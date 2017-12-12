package uppd.com.vrec.ui.activity;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import uppd.com.vrec.ui.fragment.ListRecordingsFragment;

/**
 * Created by o.rabinovych on 12/12/17.
 */

public class ListRecordingsActivity extends SingleFragmentActivity {
    @NonNull
    @Override
    protected Fragment getContentFragment() {
        return new ListRecordingsFragment();
    }
}
