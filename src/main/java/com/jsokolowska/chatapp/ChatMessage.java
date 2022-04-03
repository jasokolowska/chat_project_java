package com.jsokolowska.chatapp;

import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class ChatMessage implements Serializable {
    private String content;
    private String groupName;
    private String date;

    public ChatMessage(String content, String groupName) {
        this.content = content;
        this.groupName = groupName;
        this.date = new SimpleDateFormat().format(new Date());
    }

    public ChatMessage(String content) {
        this.content = content;
        this.groupName = null;
        this.date = new SimpleDateFormat().format(new Date());
    }
}
