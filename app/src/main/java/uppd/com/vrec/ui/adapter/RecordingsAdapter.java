package uppd.com.vrec.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import java.io.File;
import java.util.List;
import java.util.stream.IntStream;

import uppd.com.vrec.BuildConfig;
import uppd.com.vrec.R;
import uppd.com.vrec.databinding.ItemRecordingBinding;
import uppd.com.vrec.model.Recording;
import uppd.com.vrec.recorder.Recorder;
import uppd.com.vrec.service.VRecFileProvider;

/**
 * Created by o.rabinovych on 12/13/17.
 */

public class RecordingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Recording> recordings;
    private Context context;

    public RecordingsAdapter(Context context) {
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getFile().lastModified();
    }

    private Recording getItem(int position) {
        return recordings.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_recording, parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return recordings != null ? recordings.size() : 0;
    }

    public void setItems(List<Recording> recordings) {
        this.recordings = recordings;
        notifyDataSetChanged();
    }

    public void setFileSent(File file) {
        IntStream.range(0, recordings.size())
                .filter(i -> recordings.get(i).getFile().equals(file))
                .findFirst()
                .ifPresent(i -> {
                    recordings.get(i).setSent(true);
                    notifyItemChanged(i);
                });
    }

    public void addRecording(Recording recording) {
        recordings.add(recording);
        notifyItemInserted(recordings.size() - 1);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemRecordingBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.getBinding(itemView);

            RxView.clicks(itemView)
                    .subscribe(click -> {
                        final Intent intent = new Intent(Intent.ACTION_VIEW);
                        final Context context = itemView.getContext();
                        intent.setDataAndType(VRecFileProvider.getUriForFile(context, BuildConfig.FILES_AUTHORITY, binding.getItem().getFile()), Recorder.MIME_TYPE);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(intent);
                    });
        }

        public void bind(Recording item) {
            binding.setItem(item);
        }
    }
}
