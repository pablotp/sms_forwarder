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
    private SmsMessage currentSMS;
    private static final SmsSender smsSender = new SmsSender();
    private Context receiverContext = null;


    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("SMS Received");
        receiverContext = context;
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {
                    for (Object aObject : pdu_Objects) {
                        SubscriptionManager manager = context.getSystemService(SubscriptionManager.class);
                        // Permission already checked in the MainActivity
                        @SuppressLint("MissingPermission") SubscriptionInfo subscriptionInfo = manager
                                .getActiveSubscriptionInfoForSimSlotIndex(bundle.getInt("slot", -1));
                        currentSMS = getIncomingMessage(aObject, bundle);
                        storeAndForwardMessage(currentSMS, subscriptionInfo);
                    }
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

    private void storeAndForwardMessage(SmsMessage smsMessage, SubscriptionInfo subscriptionInfo) {
        AppDatabase db = AppDatabaseFactory.build(receiverContext, AppDatabaseFactory.MESSAGES_DB_NAME);
        Message m = new Message(
                smsMessage.getDisplayMessageBody(),
                smsMessage.getDisplayOriginatingAddress(),
                smsMessage.getTimestampMillis(),
                subscriptionInfo.getSimSlotIndex() + 1);
        new StoreAndForwardAsyncTask(db).execute(m);
    }

    private class StoreAndForwardAsyncTask extends AsyncTask<Message, Message, Void> {
        private AppDatabase db;

        public StoreAndForwardAsyncTask(AppDatabase userDatabase) {
            db = userDatabase;
        }

        @Override
        protected Void doInBackground(Message... messages) {
            Message message = messages[0];
            Long messageId = db.messageDao().insert(message);
            System.out.println("Stored message from: " + message.getSender());

            message.setId(messageId);
            smsSender.forwardMessage(db, message);

            return null;
        }
    }

}
