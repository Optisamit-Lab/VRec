package uppd.com.vrec.mvp;

import android.media.MediaRecorder;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import uppd.com.vrec.ui.fragment.RecorderFragment;

/**
 * Created by o.rabinovych on 12/4/17.
 */
@Module
public abstract class RecorderModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract RecorderFragment addRecorderFragment();

    @FragmentScoped
    @Binds
    abstract RecorderContract.Presenter recorderPresenter(RecorderPresenter presenter);

    @Provides
    static MediaRecorder provideAudioRecorder() {
        return  new MediaRecorder();
    }

    //NOTE:  IF you want to have something be only in the Fragment scope but not activity mark a
    //@provides or @Binds method as @FragmentScoped.  Use case is when there are multiple fragments
    //in an activity but you do not want them to share all the same objects.
}
