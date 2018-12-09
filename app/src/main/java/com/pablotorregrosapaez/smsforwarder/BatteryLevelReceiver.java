package com.pablotorregrosapaez.smsforwarder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.pablotorregrosapaez.smsforwarder.config.AppDatabase;
import com.pablotorregrosapaez.smsforwarder.factory.AppDatabaseFactory;
import com.pablotorregrosapaez.smsforwarder.model.Message;

public class BatteryLevelReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String text = context.getString(R.string.low_battery_warning);
        System.out.println(text);

        Message message = new Message(text, "INTERNAL", System.currentTimeMillis(), -1);
        new ForwardAsyncTask(context).execute(message);
    }


    private class ForwardAsyncTask extends AsyncTask<Message, Message, Void> {
        private AppDatabase db;
        private SmsSender smsSender;

        public ForwardAsyncTask(Context context) {
            smsSender = new SmsSender(context);
            db = AppDatabaseFactory.build(context, AppDatabaseFactory.MESSAGES_DB_NAME);
        }

        @Override
        protected Void doInBackground(Message... messages) {
            Message message = messages[0];
            smsSender.forwardMessage(message);
            return null;
        }
    }
}
