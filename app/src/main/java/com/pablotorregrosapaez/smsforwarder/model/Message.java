package com.pablotorregrosapaez.smsforwarder.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Message {
    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "sender")
    private String sender;

    @ColumnInfo(name = "sim_id")
    private int simId;

    @ColumnInfo(name = "received_at")
    private long receivedAt;

    @ColumnInfo(name = "forwarded_to")
    private String forwardedTo;

    @ColumnInfo(name = "forwarded_at")
    private long forwardedAt;

    public Message(String content, String sender, long receivedAt, int simId) {
        this.content = content;
        this.sender = sender;
        this.receivedAt = receivedAt;
        this.simId = simId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public int getSimId() {
        return simId;
    }

    public void setSimId(int simId) {
        this.simId = simId;
    }
}
