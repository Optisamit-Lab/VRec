package uppd.com.vrec.mvp;

import android.util.Log;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import uppd.com.vrec.recorder.Recorder;
import uppd.com.vrec.service.RecordingsManager;

/**
 * Created by o.rabinovych on 12/4/17.
 */
@Singleton
public class RecorderPresenter extends BasePresenter<RecorderContract.RecorderView> implements RecorderContract.Presenter {
    @SuppressWarnings("unused")
    private static final String TAG = RecorderPresenter.class.getSimpleName();

    private Recorder recorder;
    private final RecordingsManager recordingsManager;

    @Inject
    public RecorderPresenter(Recorder recorder, RecordingsManager recordingsManager) {
        this.recorder = recorder;
        this.recordingsManager = recordingsManager;
    }

    @Override
    public void takeView(RecorderContract.RecorderView view) {
        super.takeView(view);
        recorder.recorderState().subscribe(view, t -> Log.w(TAG, t));

        view.startRecordingClicked().subscribe(getObserver(recorder::startNew));
        view.stopRecordingClicked().subscribe(getObserver(() -> {
            final File file = recorder.stop();
            recordingsManager.queueForProcessing(file);
        }));
        view.pauseRecordingClicked().subscribe(getObserver(recorder::pause));
        view.resumeRecordingClicked().subscribe(getObserver(recorder::resume));
        view.cancelRecordingClicked().subscribe(getObserver(recorder::cancel));
    }
}
