package com.jsokolowska.chatapp;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ChatGroup  {
    private String name;
    private ChatWorkers workers;
    private Set<String> userNames;
    private List<ChatMessage> messages;

    public ChatGroup(String name) {
        this.name = name;
        this.workers = new ListChatWorkers();
        this.messages = new ArrayList<>();
        this.userNames = new HashSet<>();
    }

    public void addWorker(ChatWorker chatWorker) {
        workers.add(chatWorker);
        userNames.add(chatWorker.getUserName());
    }

    public void removeWorker(ChatWorker chatWorker) {
        userNames.remove(chatWorker.getUserName());
        workers.remove(chatWorker);
    }
}
