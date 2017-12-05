package uppd.com.vrec.mvp;

import android.support.annotation.CallSuper;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by o.rabinovych on 12/4/17.
 */

abstract class BasePresenter<T> implements IPresenter<T> {
    private final CompositeDisposable disposable = new CompositeDisposable();
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
        disposable.clear();
    }

    protected <X> Observer<X> getObserver(Runnable consumer) {
        return new Observer<X>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(X x) {
                consumer.run();
            }

            @Override
            public void onError(Throwable e) {
                // Do nothing
            }

            @Override
            public void onComplete() {
                // Do nothing
            }
        };
    }
}
