package com.pablotorregrosapaez.smsforwarder;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import com.pablotorregrosapaez.smsforwarder.config.AppDatabase;
import com.pablotorregrosapaez.smsforwarder.factory.AppDatabaseFactory;
import com.pablotorregrosapaez.smsforwarder.model.Message;

public class SmsReceiver extends BroadcastReceiver {
    private Bundle bundle;
    private Context receiverContext = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("SMS Received");
        receiverContext = context;
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pduObjects = (Object[]) bundle.get("pdus");

                if (pduObjects != null) {
                    String body = getSmsBody(pduObjects, bundle);
                    String originAddress = getOriginAddres(pduObjects, bundle);
                    int simSlot = getSimSlot(context);
                    Message message = buildMessage(body, originAddress, simSlot);
                    storeAndForwardMessage(message);
                    this.abortBroadcast();
                }
            }
        }
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        String format = bundle.getString("format");
        currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);

        return currentSMS;
    }

    private String getSmsBody(Object[] pdus, Bundle bundle) {
        SmsMessage[] messages = new SmsMessage[pdus.length];
        String format = bundle.getString("format");
        String body = "";

        for (int i = 0; i < pdus.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
        }

        SmsMessage sms = messages[0];
        if (messages.length == 1 || sms.isReplace()) {
            body = sms.getDisplayMessageBody();
        } else {
            StringBuilder bodyText = new StringBuilder();
            for (int i = 0; i < messages.length; i++) {
                bodyText.append(messages[i].getMessageBody());
            }
            body = bodyText.toString();
        }
        return body;
    }

    private String getOriginAddres(Object[] pdus, Bundle bundle) {
        String format = bundle.getString("format");
        return SmsMessage.createFromPdu((byte[]) pdus[0], format).getDisplayOriginatingAddress();
    }

    private int getSimSlot(Context context) {
        SubscriptionManager manager = context.getSystemService(SubscriptionManager.class);
        int simSlotIndex = getSimSlotIndex(bundle);

        // Permission already checked in the MainActivity
        @SuppressLint("MissingPermission") SubscriptionInfo subscriptionInfo = manager
                .getActiveSubscriptionInfoForSimSlotIndex(simSlotIndex);
        return subscriptionInfo.getSimSlotIndex();
    }

    private int getSimSlotIndex(Bundle bundle) {
        int index = bundle.getInt("slot", -1);
        if (index < 0) {
            index = bundle.getInt("slot_id", -1);
        }
        if (index < 0) {
            index = bundle.getInt("subscription", -1);
        }
        return index;
    }

    private Message buildMessage(String body, String originAddress, int simIndex) {
        return new Message(
                body,
                originAddress,
                System.currentTimeMillis(),
                simIndex + 1);
    }

    private void storeAndForwardMessage(Message message) {
        new StoreAndForwardAsyncTask(receiverContext).execute(message);
    }

    private class StoreAndForwardAsyncTask extends AsyncTask<Message, Message, Void> {
        private AppDatabase db;
        private SmsSender smsSender;

        public StoreAndForwardAsyncTask(Context context) {
            smsSender = new SmsSender(context);
            db = AppDatabaseFactory.build(context, AppDatabaseFactory.MESSAGES_DB_NAME);
        }

        @Override
        protected Void doInBackground(Message... messages) {
            Message message = messages[0];
            Long messageId = db.messageDao().insert(message);
            System.out.println("Stored message from: " + message.getSender());

            message.setId(messageId);
            smsSender.forwardMessage(message);

            return null;
        }
    }

}
