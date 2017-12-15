package uppd.com.vrec.mvp;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileDescriptor;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import uppd.com.vrec.event.RecordingSentEvent;
import uppd.com.vrec.model.Recording;
import uppd.com.vrec.recorder.FileManager;
import uppd.com.vrec.service.RecordingsManager;

/**
 * Created by o.rabinovych on 12/13/17.
 */

public class ListRecordingsPresenter extends BasePresenter<ListRecordingsContract.View> implements ListRecordingsContract.Presenter {
    private FileManager fileManager;

    private RecordingsManager recordingsManager;

    private Context context;

    @Inject
    public ListRecordingsPresenter(FileManager fileManager, RecordingsManager recordingsManager, Context context) {
        this.fileManager = fileManager;
        this.recordingsManager = recordingsManager;
        this.context = context;
    }

    @Override
    public void takeView(ListRecordingsContract.View view) {
        super.takeView(view);
        EventBus.getDefault().register(this);
        view.setList(generateRecordingsList());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecordingSent(RecordingSentEvent event) {
        view.onFileSent(event.getFile());
    }

    private List<Recording> generateRecordingsList() {
        return Stream.of(fileManager.getAllFiles())
                .map(file -> new Recording(file, recordingsManager.isSent(file), getFileDuration(file)))
                .collect(Collectors.toList());
    }

    private int getFileDuration(File file) {
        final Uri uri = Uri.fromFile(file);
        final MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(context, uri);
        } catch (RuntimeException e) {
            return 0;
        }
        final String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return durationStr != null ? Math.round(Integer.parseInt(durationStr) / 1000) : 0;
    }

    @Override
    public void dropView() {
        EventBus.getDefault().unregister(this);
        super.dropView();
    }

    @Override
    public void onSendClicked(Recording recording) {
        recordingsManager.sendNow(recording.getFile());
    }

    @Override
    public void onDeleteClicked(Recording recording) {
        recordingsManager.deleteRecording(recording.getFile());
        view.onRecordingDeleted(recording);
    }
}
