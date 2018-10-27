package com.pablotorregrosapaez.smsforwarder;

import android.telephony.SmsManager;

import com.pablotorregrosapaez.smsforwarder.config.AppDatabase;
import com.pablotorregrosapaez.smsforwarder.model.Message;

public class SmsSender {

    private String FORWARD_TO = ""; //Get from settings

    public SmsSender() {
    }

    public void forwardMessage(AppDatabase db, Message message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(FORWARD_TO, null, "FWD: " + message.getContent(), null, null);

        message.setForwardedAt(System.currentTimeMillis());
        message.setForwardedTo(FORWARD_TO);
        db.messageDao().insert(message);

        System.out.println("Forwarded message from: " + message.getSender() + " to: " + FORWARD_TO);
    }

}
