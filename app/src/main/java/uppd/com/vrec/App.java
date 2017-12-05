package uppd.com.vrec;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import uppd.com.vrec.di.DaggerAppComponent;

/**
 * Created by o.rabinovych on 12/4/17.
 */

public class App extends DaggerApplication {
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}
