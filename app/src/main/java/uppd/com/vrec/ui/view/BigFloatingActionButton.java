package uppd.com.vrec.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.BigFabResources;
import android.util.AttributeSet;


/**
 * Created by o.rabinovych on 12/17/17.
 */

public class BigFloatingActionButton extends FloatingActionButton {
    private Resources resources;

    public BigFloatingActionButton(Context context) {
        super(context);
    }

    public BigFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BigFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public Resources getResources() {
        if (resources == null) {
            resources = new BigFabResources(super.getResources());
        }
        return resources;
    }
}
