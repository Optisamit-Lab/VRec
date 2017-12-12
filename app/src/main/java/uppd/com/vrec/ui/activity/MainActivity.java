package uppd.com.vrec.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import uppd.com.vrec.R;
import uppd.com.vrec.databinding.ActivitySingleFragmentBinding;
import uppd.com.vrec.ui.fragment.RecorderFragment;

public class MainActivity extends SingleFragmentActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_viewList:
                startActivity(new Intent(this, ListRecordingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    protected Fragment getContentFragment() {
        return new RecorderFragment();
    }
}
