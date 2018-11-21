package com.pablotorregrosapaez.smsforwarder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.pablotorregrosapaez.smsforwarder.model.Message;
import com.pablotorregrosapaez.smsforwarder.utils.MessageBodyUtils;

public class MailSender {
    private Context context;

    public MailSender(Context context) {
        this.context = context;
    }

    public void forwardMessage(Message message) {
        if (fetchEnabledGmailForwarding()) {
            if (!isUsernameAndPasswordConfigured()) {
                System.out.println("Please configure the Username and Password.");
            }

            sendEmail(
                    "SMS received from " + message.getSender(),
                    MessageBodyUtils.addOriginToContent(message)
            );

            System.out.println("Forwarded message from: " + message.getSender() + " to: " + fetchUsername());
        } else {
            System.out.println("Forwarding disabled. The message cannot be forwarded.");
        }
    }

    private void sendEmail(String subject, String body) {
        BackgroundMail.newBuilder(context)
                .withUsername(fetchUsername())
                .withPassword(fetchPassword())
                .withMailto(fetchUsername())
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject(subject)
                .withBody(body)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        //do some magic
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        //do some magic
                    }
                })
                .withProcessVisibility(false)
                .send();
    }

    private boolean isUsernameAndPasswordConfigured() {
        return fetchUsername() != null && fetchPassword() != null;
    }

    private String fetchUsername() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_gmail_username), "");
    }

    private String fetchPassword() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_gmail_password), "");
    }

    private Boolean fetchEnabledGmailForwarding() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_gmail_forward_switch), false);
    }
}
