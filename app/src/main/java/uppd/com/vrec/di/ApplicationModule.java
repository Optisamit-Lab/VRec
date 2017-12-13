package uppd.com.vrec.di;

import android.app.Application;
import android.content.Context;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import uppd.com.vrec.service.RecordingsManager;

/**
 * Created by o.rabinovych on 12/4/17.

 * This is a Dagger module. We use this to bind our Application class as a Context in the AppComponent
 * By using Dagger Android we do not need to pass our Application instance to any module,
 * we simply need to expose our Application as Context.
 * One of the advantages of Dagger.Android is that your
 * Application & Activities are provided into your graph for you.
 * {@link
 * AppComponent}.
 */
@Module
public abstract class ApplicationModule {
    //expose Application as an injectable context
    @Binds
    abstract Context bindContext(Application application);

    @Provides
    static JobManager provideJobManager(Context context) {
        final Configuration conf = new Configuration.Builder(context)
                .build();
        return new JobManager(conf);
    }

    @Provides
    @Singleton
    static RecordingsManager provideRecordingsManager(Context context, JobManager jobManager) {
        return new RecordingsManager(context, jobManager);
    }
}
