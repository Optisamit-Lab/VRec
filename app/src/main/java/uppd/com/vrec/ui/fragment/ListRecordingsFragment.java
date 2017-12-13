package uppd.com.vrec.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.jakewharton.rxbinding2.widget.RxAbsListView;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import uppd.com.vrec.R;
import uppd.com.vrec.databinding.FragmentListRecordingsBinding;
import uppd.com.vrec.model.Recording;
import uppd.com.vrec.mvp.IPresenter;
import uppd.com.vrec.mvp.ListRecordingsContract;
import uppd.com.vrec.ui.adapter.RecordingsAdapter;

/**
 * Created by o.rabinovych on 12/12/17.
 */

public class ListRecordingsFragment extends BaseFragment<FragmentListRecordingsBinding> implements ListRecordingsContract.View {
    @Inject
    ListRecordingsContract.Presenter presenter;

    private RecordingsAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_recordings;
    }

    @Override
    protected IPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new RecordingsAdapter(getContext());

        binding.list.setAdapter(adapter);
    }

    @Override
    public void setList(List<Recording> recordings) {
        adapter.setItems(recordings);
    }

    @Override
    public void onFileSent(File file) {
        adapter.setFileSent(file);
    }

    @Override
    public void onRecordingAdded(Recording recording) {
        adapter.addRecording(recording);
    }
}
