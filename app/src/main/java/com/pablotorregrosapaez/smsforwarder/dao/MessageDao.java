package com.pablotorregrosapaez.smsforwarder.dao;

import com.pablotorregrosapaez.smsforwarder.model.Message;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM message")
    LiveData<List<Message>> getAll();

    @Query("SELECT * FROM message WHERE id IN (:ids)")
    LiveData<List<Message>> loadAllByIds(int[] ids);

    @Insert
    List<Long> insertAll(Message... messages);

    @Delete
    int delete(Message message);
}
