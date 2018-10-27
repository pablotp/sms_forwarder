package com.pablotorregrosapaez.smsforwarder;

import com.pablotorregrosapaez.smsforwarder.config.AppDatabase;
import com.pablotorregrosapaez.smsforwarder.model.Message;

public class SmsSender {

    public SmsSender() {
    }

    public void forwardMessage(AppDatabase db, Message message) {
        message.setForwardedAt(1);
        message.setForwardedTo("Someone");
        db.messageDao().insert(message);
        System.out.println("Forwared message from: " + message.getSender());
    }

}
