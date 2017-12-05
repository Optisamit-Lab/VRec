package uppd.com.vrec.ui.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dagger.android.support.DaggerFragment;
import uppd.com.vrec.mvp.IPresenter;

/**
 * Created by o.rabinovych on 12/4/17.
 */

abstract class BaseFragment<B extends ViewDataBinding> extends DaggerFragment {
    protected B binding;

    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return binding.getRoot();
    }

    @LayoutRes
    protected abstract int getLayoutId();

    @Override
    public void onResume() {
        super.onResume();
        //noinspection unchecked
        getPresenter().takeView(this);
    }

    @Override
    public void onPause() {
        //noinspection unchecked
        getPresenter().dropView();
        super.onPause();
    }

    protected abstract IPresenter getPresenter();
}
