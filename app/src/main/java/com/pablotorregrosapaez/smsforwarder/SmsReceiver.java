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

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("=================================== Something received!! ===================================");
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {
                    for (Object aObject : pdu_Objects) {
                        SubscriptionManager manager = context.getSystemService(SubscriptionManager.class);
                        @SuppressLint("MissingPermission") SubscriptionInfo subscriptionInfo = manager
                                .getActiveSubscriptionInfoForSimSlotIndex(bundle.getInt("slot", -1));
                        currentSMS = getIncomingMessage(aObject, bundle);
                        storeMessage(currentSMS, subscriptionInfo, context);
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

    private void storeMessage(SmsMessage smsMessage, SubscriptionInfo subscriptionInfo, Context context) {
        AppDatabase db = AppDatabaseFactory.build(context, AppDatabaseFactory.MESSAGES_DB_NAME);
        Message m = new Message(
                smsMessage.getDisplayMessageBody(),
                smsMessage.getDisplayOriginatingAddress(),
                smsMessage.getTimestampMillis(),
                subscriptionInfo.getSimSlotIndex() + 1);
        new AddUserAsyncTask(db).execute(m);
    }

    private static class AddUserAsyncTask extends AsyncTask<Message, Void, Void> {
        private AppDatabase db;

        public AddUserAsyncTask(AppDatabase userDatabase) {
            db = userDatabase;
        }

        @Override
        protected Void doInBackground(Message... message) {
            db.messageDao().insertAll(message);
            System.out.println("Stored message from: " + message[0].getSender());

            return null;
        }
    }

}
