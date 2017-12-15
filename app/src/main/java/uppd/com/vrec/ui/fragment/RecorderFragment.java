package uppd.com.vrec.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;

import io.reactivex.Observable;
import uppd.com.vrec.R;
import uppd.com.vrec.databinding.FragmentRecorderBinding;
import uppd.com.vrec.di.ActivityScoped;
import uppd.com.vrec.mvp.IPresenter;
import uppd.com.vrec.mvp.RecorderContract;
import uppd.com.vrec.recorder.Recorder;

@ActivityScoped
public class RecorderFragment extends BaseFragment<FragmentRecorderBinding> implements RecorderContract.RecorderView {
    @Inject
    RecorderContract.Presenter presenter;

    private Recorder.RecorderState recorderState;
    private Observable<Object> btnRecClicks;

    @Inject
    public RecorderFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recorder;
    }

    @Override
    protected IPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnRecClicks = RxView.clicks(binding.btnRec).share();
    }

    @Override
    public void accept(Recorder.RecorderState recorderState) {
        //noinspection ConstantConditions
        new Handler(Looper.getMainLooper()).post(() -> {
            // We need to post this to the next handler loop,
            // otherwise both pause and resume recording can be sent from the same click
            this.recorderState = recorderState;

            switch (recorderState.getState()) {
                case Recorder.RecorderState.STATE_ERROR:
                    Toast.makeText(getContext(), R.string.msg_recordingError, Toast.LENGTH_SHORT).show();
                    // FALLTHROUGH
                case Recorder.RecorderState.STATE_IDLE:
                case Recorder.RecorderState.STATE_PAUSED:
                    binding.btnRec.setActivated(false);
                    break;
                case Recorder.RecorderState.STATE_RECORDING:
                    binding.btnRec.setActivated(true);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        });
    }

    @Override
    public Observable<?> startRecordingClicked() {
        //noinspection ConstantConditions
        return btnRecClicks
                .filter(o -> recorderState.getState() == Recorder.RecorderState.STATE_IDLE)
                .compose(new RxPermissions(getActivity()).ensure(Manifest.permission.RECORD_AUDIO))
                .filter(granted -> granted);
    }

    @Override
    public Observable<?> stopRecordingClicked() {
        return RxView.clicks(binding.btnStop);
    }

    @Override
    public Observable<?> pauseRecordingClicked() {
        return btnRecClicks
                .filter(o -> recorderState.getState() == Recorder.RecorderState.STATE_RECORDING);
    }

    @Override
    public Observable<?> resumeRecordingClicked() {
        //noinspection ConstantConditions
        return btnRecClicks
                .filter(o -> recorderState.getState() == Recorder.RecorderState.STATE_PAUSED)
                .compose(new RxPermissions(getActivity()).ensure(Manifest.permission.RECORD_AUDIO))
                .filter(granted -> granted);
    }

    @Override
    public Observable<?> cancelRecordingClicked() {
        return RxView.clicks(binding.btnCancel);
    }
}
