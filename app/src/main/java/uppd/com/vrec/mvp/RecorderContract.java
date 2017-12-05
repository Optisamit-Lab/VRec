package uppd.com.vrec.mvp;


import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import uppd.com.vrec.recorder.Recorder;

/**
 * Created by o.rabinovych on 12/4/17.
 */

public interface RecorderContract {
    interface Presenter extends IPresenter<RecorderView> {
    }

    interface RecorderView extends Consumer<Recorder.RecorderState> {
        Observable<?> startRecordingClicked();

        Observable<?> stopRecordingClicked();

        Observable<?> pauseRecordingClicked();

        Observable<?> resumeRecordingClicked();
    }
}
