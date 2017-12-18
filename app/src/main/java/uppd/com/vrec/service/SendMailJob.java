package uppd.com.vrec.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.libmailcore.Address;
import com.libmailcore.Attachment;
import com.libmailcore.MailException;
import com.libmailcore.MessageBuilder;
import com.libmailcore.MessageHeader;
import com.libmailcore.OperationCallback;
import com.libmailcore.SMTPSession;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import uppd.com.vrec.R;
import uppd.com.vrec.smtp.SmtpHelper;

/**
 * Created by o.rabinovych on 12/11/17.
 */

class SendMailJob extends Job {
    @SuppressWarnings("unused")
    private static final String TAG = SendMailJob.class.getSimpleName();

    private static final String GROUP_ID = "SendMail";
    private static final long INIT_RETRY_BACKOFF_MS = 1000;

    @Getter
    private final File file;

    private MailException exception;

    private final OperationCallback loginCallback = new LoginCallback();
    private final OperationCallback sendCallback = new SendCallback();

    public SendMailJob(File file, boolean onWiFiOnly) {
        super(getParams(file, onWiFiOnly));
        this.file = file;
    }

    private static Params getParams(File file, boolean onWiFiOnly) {
        final Params params = new Params(0)
                .setPersistent(true)
                .requireNetwork()
                .addTags(getTag(file))
                .setGroupId(GROUP_ID);
        return onWiFiOnly ? params.requireUnmeteredNetwork() : params;
    }

    public static String getTag(File file) {
        return "send " + file.getAbsolutePath();
    }

    @Override
    public void onAdded() {
    }

    @Override
    public synchronized void onRun() throws Throwable {
        final SMTPSession session = SmtpHelper.createSmtpSession(getApplicationContext());

        final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        exception = null;

        Log.d(TAG, "Logging in...");
        mainThreadHandler.post(() -> session.loginOperation().start(loginCallback));

        wait();

        if (exception == null) {
            Log.d(TAG, "Sending message...");
            mainThreadHandler.post(() -> session.sendMessageOperation(getMessageBytes()).start(sendCallback));
            wait();
        }

        if (exception != null) {
            throw exception;
        }
    }

    private byte[] getMessageBytes() {
        final MessageHeader header = new MessageHeader();
        header.setFrom(getSenderAddress());
        header.setTo(getTargetAddress());
        header.setSubject(getSubject());

        final MessageBuilder builder = new MessageBuilder();
        builder.addAttachment(createAttachment());
        builder.setHeader(header);
        builder.setTextBody(getEmailBody());

        return builder.data();
    }

    @NonNull
    private String getEmailBody() {
        return getStringPreference(R.string.key_emailBody, R.string.email_body);
    }

    @NonNull
    private String getSubject() {
        return getStringPreference(R.string.key_emailSubject, R.string.email_subject);
    }

    @NonNull
    private String getStringPreference(@StringRes int keyRes, @StringRes int defValueRes) {
        final Context context = getApplicationContext();
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(keyRes), context.getString(defValueRes));
    }

    private List<Address> getTargetAddress() {
        final Context context = getApplicationContext();
        final List<Address> result = new ArrayList<>(1);
        result.add(Address.addressWithMailbox(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.key_targetEmail), "")));
        return result;
    }

    private Address getSenderAddress() {
        final Context context = getApplicationContext();
        return Address.addressWithMailbox(
                PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.key_smtp_email), ""));
    }

    private Attachment createAttachment() {
        return Attachment.attachmentWithContentsOfFile(file.getAbsolutePath());
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        Log.w(TAG, "Send mail job cancelled. File: [" + file + "]");
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.createExponentialBackoff(runCount, INIT_RETRY_BACKOFF_MS);
    }

    private class LoginCallback implements OperationCallback, Serializable {
        @Override
        public void succeeded() {
            Log.d(TAG, "Logged in");
            synchronized (SendMailJob.this) {
                SendMailJob.this.notify();
            }
        }

        @Override
        public void failed(MailException e) {
            exception = e;
            synchronized (SendMailJob.this) {
                SendMailJob.this.notify();
            }
        }
    }

    private class SendCallback implements OperationCallback, Serializable {
        @Override
        public void succeeded() {
            Log.d(TAG, "Message sent");
            synchronized (SendMailJob.this) {
                SendMailJob.this.notify();
            }
        }

        @Override
        public void failed(MailException e) {
            exception = e;
            synchronized (SendMailJob.this) {
                SendMailJob.this.notify();
            }
        }
    }
}
