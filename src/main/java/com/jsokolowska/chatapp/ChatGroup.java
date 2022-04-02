package com.jsokolowska.chatapp;

import lombok.Data;

@Data
public class ChatGroup{
    private String name;
    private ChatWorkers workers;

    public ChatGroup(String name) {
        this.name = name;
        this.workers = new ListChatWorkers();
    }

    public void addWorker(ChatWorker chatWorker) {
        workers.add(chatWorker);
    }

    public void removeWorker(ChatWorker chatWorker) {
        workers.remove(chatWorker);
    }

}
