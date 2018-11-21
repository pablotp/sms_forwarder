package com.pablotorregrosapaez.smsforwarder.utils;

import com.pablotorregrosapaez.smsforwarder.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageBodyUtils {
    public static String addOriginToContent(Message message) {
        String contentWithOrigin = "";

        contentWithOrigin += "Original Sender: " + message.getSender() + "\n";
        contentWithOrigin += "Received in origin at: " + formatDate(message.getReceivedAt()) + "\n";
        contentWithOrigin += "---------------------- \n";
        contentWithOrigin += message.getContent();

        return contentWithOrigin;
    }

    private static String formatDate(Long timeInMillis) {
        if (timeInMillis == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(timeInMillis);
        return sdf.format(resultdate);
    }
}
