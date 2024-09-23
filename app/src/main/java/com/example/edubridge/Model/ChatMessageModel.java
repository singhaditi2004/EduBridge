package com.example.edubridge.Model;

import com.google.firebase.Timestamp;

public class ChatMessageModel {
    private String message;
    private String senderld;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String message, String senderld, Timestamp timestamp) {
        this.message = message;
        this.senderld = senderld;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderld() {
        return senderld;
    }

    public void setSenderld(String senderld) {
        this.senderld = senderld;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    private Timestamp timestamp;
}
