package uppd.com.vrec.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.KeyEvent;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.libmailcore.ConnectionType;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import uppd.com.vrec.R;

/**
 * Created by o.rabinovych on 12/5/17.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback {
    private static final int[] CONNECTION_TYPES = {
            ConnectionType.ConnectionTypeClear,
            ConnectionType.ConnectionTypeTLS,
            ConnectionType.ConnectionTypeStartTLS
    };
    private static final int DEFAULT_CONNECTION_TYPE = ConnectionType.ConnectionTypeClear;

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

    private String mapValueToSummary(ListPreference pref, String value) {
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

    @Override
    protected void onBindPreferences() {
        this.<ListPreference>onPref(R.string.key_smtp_connectionType, pref -> {
            pref.setEntryValues(Arrays.stream(CONNECTION_TYPES).mapToObj(Integer::toString).toArray(String[]::new));
            pref.setEntries(Arrays.stream(CONNECTION_TYPES).mapToObj(connType -> mapValueToSummary(pref, Integer.toString(connType))).toArray(String[]::new));
            if (pref.getValue() == null) {
                pref.setValue(Integer.toString(DEFAULT_CONNECTION_TYPE));
            }
        });

        final Iterator<Preference> prefsIterator = getPrefsIterator(getPreferenceScreen());
        StreamSupport.stream(Spliterators.spliteratorUnknownSize(prefsIterator, Spliterator.ORDERED), false)
                .filter(p -> p instanceof ListPreference)
                .map(p -> (ListPreference) p)
                .forEach(pref -> {
                    pref.setSummary(mapValueToSummary(pref, pref.getValue()));
                    pref.setOnPreferenceChangeListener((preference, newValue) -> {
                        preference.setSummary(mapValueToSummary((ListPreference)preference, (String) newValue));
                        return true;
                    });
                });
    }

    private <T extends Preference> void onPref(@StringRes int prefKeyRes, Consumer<T> consumer) {
        final Preference pref = findPreference(getString(prefKeyRes));
        if (pref != null) {
            //noinspection unchecked
            consumer.accept((T) pref);
        }
    }

    private void setRootPreferences() {
        setPreferencesFromResource(R.xml.preferences, null);
        showingRootPrefs = true;
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
}
