package com.example.edubridge.Model;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatRoomModel {
    String chatRoomId;
    List<String> userid;
    Timestamp lastMessage;
    String lastMessSendId;
    String lastMessageM;

    public String getLastMessageM() {
        return lastMessageM;
    }

    public void setLastMessageM(String lastMessageM) {
        this.lastMessageM = lastMessageM;
    }

    public ChatRoomModel() {
    }

    public ChatRoomModel(String chatRoomId, List<String> userid, Timestamp lastMessage, String lastMessSendId) {
        this.chatRoomId = chatRoomId;
        this.userid = userid;
        this.lastMessage = lastMessage;
        this.lastMessSendId = lastMessSendId;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public List<String> getUserid() {
        return userid;
    }

    public void setUserid(List<String> userid) {
        this.userid = userid;
    }

    public Timestamp getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Timestamp lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessSendId() {
        return lastMessSendId;
    }

    public void setLastMessSendId(String lastMessSendId) {
        this.lastMessSendId = lastMessSendId;
    }



}

