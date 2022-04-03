package com.jsokolowska.chatapp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatGroup {
    private String name;
    private ChatWorkers workers;
    private List<ChatMessage> messages;

    public ChatGroup(String name) {
        this.name = name;
        this.workers = new ListChatWorkers();
        this.messages = new ArrayList<>();
    }

    public void addWorker(ChatWorker chatWorker) {
        workers.add(chatWorker);
    }

    public void removeWorker(ChatWorker chatWorker) {
        workers.remove(chatWorker);
    }

}
