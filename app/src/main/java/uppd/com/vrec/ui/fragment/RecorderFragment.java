package uppd.com.vrec.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
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
    private Subject<Object> cancelClicks = PublishSubject.create();

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

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnRecClicks = RxView.clicks(binding.btnRec).share();

        RxView.clicks(binding.btnCancel)
                .subscribe(click -> new AlertDialog.Builder(getContext())
                        .setTitle(R.string.msg_title_cancel_confirm)
                        .setMessage(R.string.msg_cancel_confirm)
                        .setNegativeButton(R.string.btn_delete_no, null)
                        .setPositiveButton(R.string.btn_delete_yes, (dialogInterface, i) -> cancelClicks.onNext(click))
                        .show());
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
                    binding.btnPause.setVisibility(View.GONE);
                    binding.btnCancel.setVisibility(View.GONE);
                    // FALLTHROUGH
                case Recorder.RecorderState.STATE_PAUSED:
                    binding.btnRec.setActivated(false);
                    binding.btnPause.setEnabled(false);
                    break;
                case Recorder.RecorderState.STATE_RECORDING:
                    binding.btnRec.setActivated(true);
                    binding.btnPause.setVisibility(View.VISIBLE);
                    binding.btnCancel.setVisibility(View.VISIBLE);
                    binding.btnPause.setEnabled(true);
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
        return btnRecClicks
                .filter(o -> recorderState.getState() == Recorder.RecorderState.STATE_RECORDING);
    }

    @Override
    public Observable<?> pauseRecordingClicked() {
        return RxView.clicks(binding.btnPause)
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
        return cancelClicks;
    }
}
