package com.pablotorregrosapaez.smsforwarder.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Message {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "sender")
    private String sender;

    @ColumnInfo(name = "received_at")
    private long receivedAt;

    @ColumnInfo(name = "forwarded_to")
    private String forwardedTo;

    @ColumnInfo(name = "forwarded_at")
    private long forwardedAt;

    public Message(String content, String sender, long receivedAt) {
        this.content = content;
        this.sender = sender;
        this.receivedAt = receivedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(long receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getForwardedTo() {
        return forwardedTo;
    }

    public void setForwardedTo(String forwardedTo) {
        this.forwardedTo = forwardedTo;
    }

    public long getForwardedAt() {
        return forwardedAt;
    }

    public void setForwardedAt(long forwardedAt) {
        this.forwardedAt = forwardedAt;
    }
}
