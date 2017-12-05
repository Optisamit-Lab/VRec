package uppd.com.vrec.mvp;

import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import uppd.com.vrec.recorder.Recorder;

/**
 * Created by o.rabinovych on 12/4/17.
 */
@Singleton
public class RecorderPresenter extends BasePresenter<RecorderContract.RecorderView> implements RecorderContract.Presenter {
    @SuppressWarnings("unused")
    private static final String TAG = RecorderPresenter.class.getSimpleName();

    @Inject
    public RecorderPresenter(Recorder recorder) {
        this.recorder = recorder;
    }

    private Recorder recorder;

    @Override
    public void takeView(RecorderContract.RecorderView view) {
        super.takeView(view);
        recorder.recorderState().subscribe(view, t -> Log.w(TAG, t));

        view.startRecordingClicked().subscribe(o -> recorder.startNew());
        view.stopRecordingClicked().subscribe(o -> recorder.stop());
        view.pauseRecordingClicked().subscribe(o -> recorder.pause());
        view.resumeRecordingClicked().subscribe(o -> recorder.resume());
        view.cancelRecordingClicked().subscribe(o -> recorder.cancel());
    }
}
