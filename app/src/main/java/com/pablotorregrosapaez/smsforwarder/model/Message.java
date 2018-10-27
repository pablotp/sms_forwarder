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
    private Integer simId;

    @ColumnInfo(name = "received_at")
    private Long receivedAt;

    @ColumnInfo(name = "forwarded_to")
    private String forwardedTo;

    @ColumnInfo(name = "forwarded_at")
    private Long forwardedAt;

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

    public Integer getSimId() {
        return simId;
    }

    public void setSimId(Integer simId) {
        this.simId = simId;
    }

    public Long getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Long receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getForwardedTo() {
        return forwardedTo;
    }

    public void setForwardedTo(String forwardedTo) {
        this.forwardedTo = forwardedTo;
    }

    public Long getForwardedAt() {
        return forwardedAt;
    }

    public void setForwardedAt(Long forwardedAt) {
        this.forwardedAt = forwardedAt;
    }
}
