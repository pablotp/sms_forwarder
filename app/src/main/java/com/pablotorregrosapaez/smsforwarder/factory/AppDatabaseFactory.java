package com.pablotorregrosapaez.smsforwarder.factory;

import android.content.Context;

import com.pablotorregrosapaez.smsforwarder.config.AppDatabase;

import java.util.HashMap;
import java.util.Map;

import androidx.room.Room;

public class AppDatabaseFactory {
    public static final String MESSAGES_DB_NAME = "db-messages";
    private static Map<String, AppDatabase> register = new HashMap<>();

    public static AppDatabase build(Context context, String dbName) {
        if (!register.containsKey(dbName)) {
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, dbName).build();
            register.put(dbName, db);
        }

        return register.get(dbName);
    }
}
