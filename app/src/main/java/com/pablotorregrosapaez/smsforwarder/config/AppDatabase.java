package com.pablotorregrosapaez.smsforwarder.config;

import com.pablotorregrosapaez.smsforwarder.dao.MessageDao;
import com.pablotorregrosapaez.smsforwarder.model.Message;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Message.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MessageDao messageDao();
}