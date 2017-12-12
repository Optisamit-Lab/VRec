package uppd.com.vrec.ui.fragment;

import uppd.com.vrec.R;
import uppd.com.vrec.mvp.IPresenter;

/**
 * Created by o.rabinovych on 12/12/17.
 */

public class ListRecordingsFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_recordings;
    }

    @Override
    protected IPresenter getPresenter() {
        return null;
    }
}
