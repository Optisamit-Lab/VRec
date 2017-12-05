package uppd.com.vrec.mvp;

import android.support.annotation.CallSuper;

/**
 * Created by o.rabinovych on 12/4/17.
 */

abstract class BasePresenter<T> implements IPresenter<T> {
    protected T view;

    @Override
    @CallSuper
    public void takeView(T view) {
        this.view = view;
    }

    @Override
    @CallSuper
    public void dropView() {
        view = null;
    }
}
