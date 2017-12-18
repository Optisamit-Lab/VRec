package android.support.v7.widget;

import android.content.res.Resources;

import uppd.com.vrec.R;

/**
 * Created by o.rabinovych on 12/17/17.
 */

public class BigFabResources extends ResourcesWrapper {
    public BigFabResources(Resources resources) {
        super(resources);
    }

    @Override
    public float getDimension(int id) throws NotFoundException {
        if (id == android.support.design.R.dimen.design_fab_image_size) {
            return getDimension(R.dimen.rec_btn_size);
        } else {
            return super.getDimension(id);
        }
    }
}
