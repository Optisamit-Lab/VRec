package uppd.com.vrec.mvp;

import java.io.File;
import java.util.List;

import uppd.com.vrec.model.Recording;

/**
 * Created by o.rabinovych on 12/12/17.
 */

public interface ListRecordingsContract {
    interface Presenter extends IPresenter<View> {
        void onSendClicked(Recording recording);

        void onDeleteClicked(Recording recording);
    }
    interface View{
        void setList(List<Recording> recordings);

        void onFileSent(File file);

        void onRecordingDeleted(Recording recording);
    }
}
