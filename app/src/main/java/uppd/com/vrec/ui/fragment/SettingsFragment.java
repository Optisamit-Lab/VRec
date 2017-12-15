package uppd.com.vrec.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.libmailcore.Address;
import com.libmailcore.ConnectionType;
import com.libmailcore.MailException;
import com.libmailcore.OperationCallback;
import com.libmailcore.SMTPSession;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import uppd.com.vrec.R;
import uppd.com.vrec.recorder.FileManager;
import uppd.com.vrec.recorder.FileManager_Factory;
import uppd.com.vrec.service.RecordingsManager;
import uppd.com.vrec.smtp.SmtpHelper;

/**
 * Created by o.rabinovych on 12/5/17.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback, PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback {
    private static final int[] CONNECTION_TYPES = {
            ConnectionType.ConnectionTypeClear,
            ConnectionType.ConnectionTypeTLS,
            ConnectionType.ConnectionTypeStartTLS
    };
    private static final String DIALOG_FRAGMENT_TAG = "dft";

    private boolean showingRootPrefs;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        RxView.keys(getView(), keyEvent -> keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK && !showingRootPrefs)
                .subscribe(o -> setRootPreferences());
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setRootPreferences();
    }

    private Iterator<Preference> getPrefsIterator(PreferenceScreen preferenceScreen) {
        return new Iterator<Preference>() {
            int itemsTaken = 0;
            Iterator<Preference> childIterator = null;

            @Override
            public boolean hasNext() {
                return preferenceScreen.getPreferenceCount() > itemsTaken;
            }

            @Override
            public Preference next() {
                if (childIterator != null) {
                    final Preference next = childIterator.next();
                    if (!childIterator.hasNext()) {
                        childIterator = null;
                        itemsTaken++;
                    }
                    return next;
                }
                final Preference pref = preferenceScreen.getPreference(itemsTaken);
                if (pref instanceof PreferenceScreen) {
                    final Iterator<Preference> childIterator = getPrefsIterator((PreferenceScreen) pref);
                    if (childIterator.hasNext()) {
                        this.childIterator = childIterator;
                    } else {
                        itemsTaken++;
                    }
                } else {
                    itemsTaken++;
                }
                return pref;
            }
        };
    }

    private String mapConnTypeValueToSummary(Preference pref, String value) {
        if (pref.getKey().equals(getString(R.string.key_smtp_connectionType))) {
            @StringRes final int strRes;
            switch (Integer.valueOf(value)) {
                case ConnectionType.ConnectionTypeClear:
                    strRes = R.string.pref_smtp_ConnectionType_clear;
                    break;
                case ConnectionType.ConnectionTypeTLS:
                    strRes = R.string.pref_smtp_ConnectionType_tls;
                    break;
                case ConnectionType.ConnectionTypeStartTLS:
                    strRes = R.string.pref_smtp_ConnectionType_startTls;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            return getString(strRes);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private <T extends Preference> void onPref(@StringRes int prefKeyRes, Consumer<T> consumer) {
        final Preference pref = findPreference(prefKeyRes);
        if (pref != null) {
            //noinspection unchecked
            consumer.accept((T) pref);
        }
    }

    private void setRootPreferences() {
        setPreferencesFromResource(R.xml.preferences, null);
        showingRootPrefs = true;

        this.<ListPreference>onPref(R.string.key_smtp_connectionType, pref -> {
            pref.setEntryValues(Arrays.stream(CONNECTION_TYPES)
                    .mapToObj(Integer::toString)
                    .toArray(String[]::new));
            pref.setEntries(Arrays.stream(CONNECTION_TYPES)
                    .mapToObj(connType -> mapConnTypeValueToSummary(pref, Integer.toString(connType)))
                    .toArray(String[]::new));

            if (pref.getValue() == null) {
                pref.setValue(Integer.toString(SmtpHelper.DEFAULT_CONN_TYPE));
            }

            pref.setSummary(mapConnTypeValueToSummary(pref, pref.getValue()));
            pref.setOnPreferenceChangeListener((preference, newValue) -> {
                preference.setSummary(mapConnTypeValueToSummary(preference, (String) newValue));
                return true;
            });
        });

        this.<ListPreference>onPref(R.string.key_deleteRecordingsAfter, pref -> {
            pref.setEntryValues(Stream.of(RecordingsManager.DeleteAfter.values())
                    .map(RecordingsManager.DeleteAfter::name)
                    .toArray(CharSequence[]::new));
            pref.setEntries(Stream.of(RecordingsManager.DeleteAfter.values())
                    .map(value -> getString(value.getStrRes()))
                    .toArray(CharSequence[]::new));

            if (pref.getValue() == null) {
                pref.setValue(RecordingsManager.DeleteAfter.Immediately.name());
            }

            pref.setSummary(getString(RecordingsManager.DeleteAfter.valueOf(pref.getValue()).getStrRes()));
            pref.setOnPreferenceChangeListener((preference, newValue) -> {
                preference.setSummary(getString(RecordingsManager.DeleteAfter.valueOf((String)newValue).getStrRes()));
                return true;
            });
        });

        this.<ListPreference>onPref(R.string.key_storeTo, pref -> {
            if (FileManager_Factory.create(this::getContext).get().extStorageAvailable()) {
                pref.setEntryValues(new CharSequence[]{String.valueOf(FileManager.STORE_TO_INTERNAL), String.valueOf(FileManager.STORE_TO_EXTERNAL)});
                pref.setEntries(new CharSequence[]{getString(R.string.pref_storeTo_internal), getString(R.string.pref_storeTo_external)});

                if (pref.getValue() == null) {
                    pref.setValue(String.valueOf(FileManager.STORE_TO_INTERNAL));
                }

                pref.setSummary(getString(pref.getValue().equals(String.valueOf(FileManager.STORE_TO_INTERNAL)) ? R.string.pref_storeTo_internal : R.string.pref_storeTo_external));
                pref.setOnPreferenceChangeListener((preference, newValue) -> {
                    preference.setSummary(getString(newValue.equals(String.valueOf(FileManager.STORE_TO_INTERNAL)) ? R.string.pref_storeTo_internal : R.string.pref_storeTo_external));
                    return true;
                });
            } else {
                pref.setVisible(false);
            }
        });

        final Iterable<Preference> prefsIterator = () -> getPrefsIterator(getPreferenceScreen());
        StreamSupport.stream(prefsIterator.spliterator(), false)
                .filter(pref -> pref instanceof EditTextPreference)
                .filter(pref -> !pref.getKey().equals(getString(R.string.key_smtp_password)))
                .map(pref -> (EditTextPreference) pref)
                .forEach(pref -> {
                    pref.setSummary(pref.getText());
                    pref.setOnPreferenceChangeListener((preference, newValue) -> {
                        preference.setSummary((CharSequence) newValue);
                        return true;
                    });
                });

        onPref(R.string.key_smtp_test, preference -> preference.setOnPreferenceClickListener(pref -> {
            startSmtpTest();
            preference.setEnabled(false);
            return true;
        }));
    }

    private void startSmtpTest() {
        final SMTPSession session = SmtpHelper.createSmtpSession(getContext());
        session.checkAccountOperation(Address.addressWithMailbox(getEmail())).start(new OperationCallback() {
            @Override
            public void succeeded() {
                onPref(R.string.key_smtp_test, pref -> pref.setEnabled(true));
                Toast.makeText(getContext(), R.string.msg_smtp_connection_good, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failed(MailException e) {
                onPref(R.string.key_smtp_test, pref -> pref.setEnabled(true));
                final String errorDescription = SmtpHelper.getErrorDescription(e);
                Toast.makeText(getContext(), errorDescription != null ? errorDescription : getString(R.string.msg_smtp_unknown_error, e.errorCode()), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getEmail() {
        return getPreferenceManager().getSharedPreferences().getString(getString(R.string.key_smtp_email), "");
    }

    private <T extends Preference> T findPreference(@StringRes int prefNameRes) {
        //noinspection unchecked
        return (T) findPreference(getString(prefNameRes));
    }

    @Override
    public Fragment getCallbackFragment() {
        return this;
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat caller, PreferenceScreen pref) {
        if (pref.getKey().equals(getString(R.string.key_smtp))) {
            setPreferenceScreen(pref);
            showingRootPrefs = false;
            return true;
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onPreferenceDisplayDialog(@NonNull PreferenceFragmentCompat caller, Preference pref) {
        if (pref instanceof EditTextPreference) {
            if (caller.getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) == null) {
                final DialogFragment fragment = CustomEditTextPreferenceFragment.instantiate(pref);
                fragment.setTargetFragment(this, 0);
                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            }

            return true;
        } else {
            return false;
        }
    }
}
