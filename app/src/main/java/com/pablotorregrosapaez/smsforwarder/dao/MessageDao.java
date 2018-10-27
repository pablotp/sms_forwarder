package com.pablotorregrosapaez.smsforwarder.dao;

import com.pablotorregrosapaez.smsforwarder.model.Message;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM message")
    LiveData<List<Message>> getAll();

    @Query("SELECT * FROM message WHERE id IN (:ids)")
    LiveData<List<Message>> findByIds(Long[] ids);

    @Query("SELECT * FROM message WHERE id = :id LIMIT 1")
    LiveData<Message> findById(Long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(Message message);

    @Insert
    List<Long> insertAll(Message... messages);

    @Delete
    int delete(Message message);
}
