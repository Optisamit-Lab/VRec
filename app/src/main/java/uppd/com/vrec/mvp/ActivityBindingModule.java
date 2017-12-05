package uppd.com.vrec.mvp;


import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import uppd.com.vrec.di.ActivityScoped;
import uppd.com.vrec.ui.activity.MainActivity;
import uppd.com.vrec.ui.activity.SettingsActivity;

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module ActivityBindingModule is on,
 * in our case that will be AppComponent. The beautiful part about this setup is that you never need to tell AppComponent that it is going to have all these subcomponents
 * nor do you need to tell these subcomponents that AppComponent exists.
 * We are also telling Dagger.Android that this generated SubComponent needs to include the specified modules and be aware of a scope annotation @ActivityScoped
 */
@Module
public abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = RecorderModule.class)
    abstract MainActivity addMainActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract SettingsActivity addSettingsActivity();
}
