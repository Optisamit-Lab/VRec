package uppd.com.vrec.smtp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.libmailcore.AuthType;
import com.libmailcore.ConnectionLogger;
import com.libmailcore.ConnectionType;
import com.libmailcore.ErrorCode;
import com.libmailcore.MailException;
import com.libmailcore.SMTPSession;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.stream.Stream;

import uppd.com.vrec.R;


import static uppd.com.vrec.util.Safety.safePredicate;

/**
 * Created by o.rabinovych on 12/9/17.
 */

public class SmtpHelper {
    @SuppressWarnings("unused")
    private static final String TAG = SmtpHelper.class.getSimpleName();

    public static final int DEFAULT_CONN_TYPE = ConnectionType.ConnectionTypeClear;

    private SmtpHelper() {
    }

    public static SMTPSession createSmtpSession(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        final SMTPSession session = new SMTPSession();

        session.setConnectionType(Integer.valueOf(prefs.getString(context.getString(R.string.key_smtp_connectionType), Integer.toString(DEFAULT_CONN_TYPE))));
        session.setCheckCertificateEnabled(prefs.getBoolean(context.getString(R.string.key_smtp_checkCertificate), context.getResources().getBoolean(R.bool.pref_smtp_checkCertificate)));
        session.setHostname(prefs.getString(context.getString(R.string.key_smtp_hostName), ""));
        final String port = prefs.getString(context.getString(R.string.key_smtp_port), "");
        try {
            session.setPort(Integer.valueOf(port));
        } catch (NumberFormatException e) {
            Log.w(TAG, "Port number in invalid format");
        }
        session.setUsername(prefs.getString(context.getString(R.string.key_smtp_username), ""));
        session.setPassword(prefs.getString(context.getString(R.string.key_smtp_password), ""));

        return session;
    }

    @Nullable
    public static String getErrorDescription(MailException e) {
        if (!TextUtils.isEmpty(e.getLocalizedMessage())) {
            return e.getLocalizedMessage();
        }
        return Stream.of(ErrorCode.class.getDeclaredFields())
                .filter(field -> field.getType() == int.class)
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(safePredicate(field -> field.getInt(null) == e.errorCode()))
                .findFirst()
                .map(Field::getName)
                .orElse(null);
    }
}
