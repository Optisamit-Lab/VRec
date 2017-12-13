package uppd.com.vrec.mvp;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import uppd.com.vrec.ui.fragment.ListRecordingsFragment;

/**
 * Created by o.rabinovych on 12/13/17.
 */
@Module
public abstract class ListRecordingsModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract ListRecordingsFragment addListRecordingsFragment();

    @FragmentScoped
    @Binds
    abstract ListRecordingsContract.Presenter presenter(ListRecordingsPresenter presenter);
}
