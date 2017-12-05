package uppd.com.vrec.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import uppd.com.vrec.App;
import uppd.com.vrec.mvp.ActivityBindingModule;
import uppd.com.vrec.mvp.RecorderModule;

/**
 * Created by o.rabinovych on 12/4/17.
 */
@Singleton
@Component(modules = {AndroidSupportInjectionModule.class, ActivityBindingModule.class, ApplicationModule.class, RecorderModule.class})
public interface AppComponent extends AndroidInjector<App> {
    // Gives us syntactic sugar. we can then do DaggerAppComponent.builder().application(this).build().inject(this);
    // never having to instantiate any modules or say which module we are passing the application to.
    // Application will just be provided into our app graph now.
    @Component.Builder
    interface Builder {
        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
