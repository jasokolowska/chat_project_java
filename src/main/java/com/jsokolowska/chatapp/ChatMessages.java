package com.jsokolowska.chatapp;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ChatMessages implements Serializable {

    private List<ChatMessage> messages = new LinkedList<>();

    public void save(ChatMessage chatMessage){
        messages.add(chatMessage);
    }

    public List<ChatMessage> getAll() {
        return messages;
    }
}
