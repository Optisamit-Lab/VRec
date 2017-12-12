package uppd.com.vrec.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.preference.PreferenceManager;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.callback.JobManagerCallback;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Getter;
import uppd.com.vrec.R;

/**
 * Created by o.rabinovych on 12/11/17.
 */
@Singleton
public class RecordingsManager implements JobManagerCallback {
    private static final long MS_IN_WEEK = 20/*7 * 24 * 3600 */* 1000;
    private Context context;

    private JobManager jobManager;

    @Inject
    public RecordingsManager(Context context, JobManager jobManager) {
        this.context = context;
        this.jobManager = jobManager;
        jobManager.addCallback(this);
    }


    public void queueForProcessing(File file) {
        final Job job = new SendMailJob(file,
                PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.key_wifiOnly), context.getResources().getBoolean(R.bool.pref_wifiOnly)));
        jobManager.addJobInBackground(job);
    }

    @Override
    public void onJobAdded(@NonNull Job job) {
    }

    @Override
    public void onJobRun(@NonNull Job job, int resultCode) {
    }

    @Override
    public void onJobCancelled(@NonNull Job job, boolean byCancelRequest, @Nullable Throwable throwable) {
    }

    @Override
    public void onDone(@NonNull Job job) {
    }

    @Override
    public void onAfterJobRun(@NonNull Job job, int resultCode) {
        if (job instanceof SendMailJob && resultCode == JobManagerCallback.RESULT_SUCCEED) {
            final DeleteAfter deleteAfter = DeleteAfter.valueOf(PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(context.getString(R.string.key_deleteRecordingsAfter), ""));

            final long deleteDelayMs;

            switch (deleteAfter) {
                case Immediately:
                    deleteDelayMs = 0;
                    break;
                case Week:
                    deleteDelayMs = MS_IN_WEEK;
                    break;
                case Never:
                default:
                    return;
            }

            jobManager.addJobInBackground(new DeleteRecordingJob(((SendMailJob)job).getFile(), deleteDelayMs));
        }
    }

    public enum DeleteAfter {
        Never(R.string.pref_deleteAfter_never), Immediately(R.string.pref_deleteAfter_immediately), Week(R.string.pref_deleteAfter_week);

        @Getter
        private final int strRes;

        DeleteAfter(@StringRes int strRes) {
            this.strRes = strRes;
        }
    }
}
