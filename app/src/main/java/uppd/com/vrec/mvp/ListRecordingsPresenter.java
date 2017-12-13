package uppd.com.vrec.mvp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import uppd.com.vrec.event.RecordingAddedEvent;
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

    @Inject
    public ListRecordingsPresenter(FileManager fileManager, RecordingsManager recordingsManager) {
        this.fileManager = fileManager;
        this.recordingsManager = recordingsManager;
    }

    @Override
    public void takeView(ListRecordingsContract.View view) {
        super.takeView(view);
        EventBus.getDefault().register(this);
        view.setList(generateRecordingsList());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecordingAdded(RecordingAddedEvent event) {
        view.onRecordingAdded(event.getRecording());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecordingSent(RecordingSentEvent event) {
        view.onFileSent(event.getFile());
    }

    private List<Recording> generateRecordingsList() {
        return Stream.of(fileManager.getAllFiles())
                .map(file -> new Recording(file, recordingsManager.isSent(file)))
                .collect(Collectors.toList());
    }

    @Override
    public void dropView() {
        EventBus.getDefault().unregister(this);
        super.dropView();
    }
}
