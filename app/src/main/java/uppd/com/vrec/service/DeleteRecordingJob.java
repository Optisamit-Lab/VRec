package uppd.com.vrec.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import java.io.File;

/**
 * Created by o.rabinovych on 12/12/17.
 */

class DeleteRecordingJob extends Job{
    private static final long INIT_RETRY_BACKOFF_MS = 1000;

    private final File file;

    public DeleteRecordingJob(File file, long delayMs) {
        super(new Params(0).delayInMs(delayMs));
        this.file = file;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        if (file.exists() && !file.delete()) {
            throw new RuntimeException("Failed to delete file " + file);
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.createExponentialBackoff(runCount, INIT_RETRY_BACKOFF_MS);
    }
}
