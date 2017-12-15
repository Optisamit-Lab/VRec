package uppd.com.vrec.recorder;

import android.media.MediaRecorder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import lombok.RequiredArgsConstructor;
import uppd.com.vrec.exception.RecordingException;
import uppd.com.vrec.model.Recording;

/**
 * Created by o.rabinovych on 12/4/17.
 */

public class Recorder {
    public final static String MIME_TYPE = "audio/aac";

    @SuppressWarnings("unused")
    private static final String TAG = Recorder.class.getSimpleName();

    private FileManager fileManager;
    private MediaRecorder audioRecorder;

    private Subject<RecorderState> observable;

    @RecorderState.State
    private int state = RecorderState.STATE_IDLE;

    @Nullable
    private File file;

    @Inject
    Recorder(MediaRecorder audioRecorder, FileManager fileManager) {
        this.fileManager = fileManager;
        this.audioRecorder = audioRecorder;
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
        initRecorder();
        try {
            file = fileManager.getNewFile();

            audioRecorder.setOutputFile(file.getAbsolutePath());
            audioRecorder.prepare();
            audioRecorder.start();
            state = RecorderState.STATE_RECORDING;
        } catch (IllegalStateException | IOException e) {
            state = RecorderState.STATE_ERROR;
            observable.onNext(adaptRecorderState());
            audioRecorder.reset();
            state = RecorderState.STATE_IDLE;
        } finally {
            observable.onNext(adaptRecorderState());
        }
    }

    public File stop() {
        final File result = file;

        try {
            if (state == RecorderState.STATE_PAUSED) {
                audioRecorder.resume();
                // If we don't do this, the recorder freezes. At least on Nexus 5x
            }
            audioRecorder.stop();

            file = null;

            state = RecorderState.STATE_IDLE;
        } catch (IllegalStateException e) {
            state = RecorderState.STATE_ERROR;
        }
        observable.onNext(adaptRecorderState());

        return result;
    }

    public void pause() {
        try {
            audioRecorder.pause();
            state = RecorderState.STATE_PAUSED;
        } catch (IllegalStateException e) {
            state = RecorderState.STATE_ERROR;
        }
        observable.onNext(adaptRecorderState());
    }

    public void resume() {
        try {
            audioRecorder.resume();
            state = RecorderState.STATE_RECORDING;
        } catch (IllegalStateException e) {
            state = RecorderState.STATE_ERROR;
        }
        observable.onNext(adaptRecorderState());
    }

    public void cancel() {
        final File file = this.file;
        assert file != null;
        stop();
        if (!file.delete()) {
            Log.w(TAG, "Failed to delete file");
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
