package uppd.com.vrec.ui.fragment;

import android.os.Bundle;
import android.support.v7.preference.EditTextPreferenceDialogFragmentCompat;
import android.support.v7.preference.Preference;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import uppd.com.vrec.R;

/**
 * Created by o.rabinovych on 12/8/17.
 */

public class CustomEditTextPreferenceFragment extends EditTextPreferenceDialogFragmentCompat {
    public static CustomEditTextPreferenceFragment instantiate(Preference pref) {
        final CustomEditTextPreferenceFragment
                fragment = new CustomEditTextPreferenceFragment();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, pref.getKey());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        final EditText editText = view.findViewById(android.R.id.edit);
        final String key = getPreference().getKey();
        if (key.equals(getString(R.string.key_smtp_hostName))) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        } else if (key.equals(getString(R.string.key_smtp_password))) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        } else if (key.equals(getString(R.string.key_smtp_port))) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (key.equals(getString(R.string.key_smtp_username))) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (key.equals(getString(R.string.key_smtp_email)) || key.equals(getString(R.string.key_targetEmail))) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        editText.setSelectAllOnFocus(true);
    }
}
