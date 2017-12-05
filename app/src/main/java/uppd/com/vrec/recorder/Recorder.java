package uppd.com.vrec.recorder;

import android.media.MediaRecorder;
import android.support.annotation.IntDef;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import lombok.RequiredArgsConstructor;
import uppd.com.vrec.exception.RecordingException;

/**
 * Created by o.rabinovych on 12/4/17.
 */

public class Recorder {
    @SuppressWarnings("unused")
    private static final String TAG = Recorder.class.getSimpleName();

    private FileManager fileManager;
    private MediaRecorder audioRecorder;

    private Subject<RecorderState> observable;

    @RecorderState.State private int state = RecorderState.STATE_IDLE;

    @Inject
    Recorder(MediaRecorder audioRecorder, FileManager fileManager) {
        this.fileManager = fileManager;
        this.audioRecorder = audioRecorder;
        initRecorder();
        observable = BehaviorSubject.createDefault(adaptRecorderState());
    }

    private void initRecorder() {
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    }

    public Observable<RecorderState> recorderState() {
        return observable;
    }

    public void startNew() {
        try {
            audioRecorder.setOutputFile(fileManager.getNewFile().getAbsolutePath());
            audioRecorder.prepare();
            audioRecorder.start();
            state = RecorderState.STATE_RECORDING;
            observable.onNext(adaptRecorderState());
        } catch (IllegalStateException | IOException e) {
            state = RecorderState.STATE_ERROR;
            observable.onError(new RecordingException(e));
        }
    }

    public void stop() {
        try {
            audioRecorder.stop();
            initRecorder();
            state = RecorderState.STATE_IDLE;
            observable.onNext(adaptRecorderState());
        } catch (IllegalStateException e) {
            state = RecorderState.STATE_ERROR;
            observable.onError(new RecordingException(e));
        }
    }

    public void pause() {
        try {
            audioRecorder.pause();
            state = RecorderState.STATE_PAUSED;
            observable.onNext(adaptRecorderState());
        } catch (IllegalStateException e) {
            state = RecorderState.STATE_ERROR;
            observable.onError(new RecordingException(e));
        }
    }

    public void resume() {
        try {
            audioRecorder.resume();
            state =RecorderState.STATE_RECORDING;
            observable.onNext(adaptRecorderState());
        } catch (IllegalStateException e) {
            state = RecorderState.STATE_ERROR;
            observable.onError(new RecordingException(e));
        }
    }

    private RecorderState adaptRecorderState() {
        return new RecorderState(state);
    }

    @RequiredArgsConstructor
    public static class RecorderState {
        public static final int STATE_ERROR = -1;
        public static final int STATE_IDLE = 0;
        public static final int STATE_RECORDING = 1;
        public static final int STATE_PAUSED = 2;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({STATE_IDLE, STATE_RECORDING, STATE_PAUSED, STATE_ERROR})
        public @interface State {
        }

        final int state;

        @State
        public int getState() {
            return state;
        }
    }
}
