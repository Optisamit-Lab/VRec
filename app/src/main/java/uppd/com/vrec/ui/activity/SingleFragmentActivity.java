package uppd.com.vrec.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import uppd.com.vrec.R;
import uppd.com.vrec.databinding.ActivitySingleFragmentBinding;

/**
 * Created by o.rabinovych on 12/5/17.
 */

abstract class SingleFragmentActivity extends BaseActivity {
    private ActivitySingleFragmentBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_fragment);

        setSupportActionBar(binding.toolbar);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, getContentFragment())
                .commit();

    }

    @NonNull
    protected abstract Fragment getContentFragment();
}
