package com.pablotorregrosapaez.smsforwarder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

import com.pablotorregrosapaez.smsforwarder.config.AppDatabase;
import com.pablotorregrosapaez.smsforwarder.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsSender {

    private String forwardTo;
    private Context context;

    public SmsSender(Context context) {
        this.context = context;
        forwardTo = fetchPhoneNumber();
    }

    public void forwardMessage(AppDatabase db, Message message) {
        if (forwardTo.isEmpty()) {
            System.err.println("Configure a phone number.");
            return;
        }

        if (fetchEnabledForwarding()) {
            SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(Integer.parseInt(fetchSimId()));
            smsManager.sendTextMessage(forwardTo, null, addOriginToContent(message), null, null);

            message.setForwardedAt(System.currentTimeMillis());
            message.setForwardedTo(forwardTo);
            db.messageDao().insert(message);

            System.out.println("Forwarded message from: " + message.getSender() + " to: " + forwardTo);
        } else {
            System.out.println("Forwarding disabled. The message cannot be forwarded.");
        }
    }

    private String addOriginToContent(Message message) {
        String contentWithOrigin = "";

        contentWithOrigin += "Original Sender: " + message.getSender() + "\n";
        contentWithOrigin += "Received in origin at: " + formatDate(message.getReceivedAt()) + "\n";
        contentWithOrigin += "---------------------- \n";
        contentWithOrigin += message.getContent();

        return contentWithOrigin;
    }

    private String formatDate(Long timeInMillis) {
        if (timeInMillis == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(timeInMillis);
        return sdf.format(resultdate);
    }

    private String fetchSimId() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_sim_list), "");
    }

    private String fetchPhoneNumber() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_phone_number), "");
    }

    private Boolean fetchEnabledForwarding() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_forward_switch), false);
    }
}
