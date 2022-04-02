package com.jsokolowska.chatapp;

import java.util.ArrayList;
import java.util.List;

public class ListChatWorkers implements ChatWorkers {

    private final List<ChatWorker> chatWorkers = new ArrayList<>();

    @Override
    public void add(ChatWorker chatWorker) {
        chatWorkers.add(chatWorker);
    }

    @Override
    public void remove(ChatWorker chatWorker) {
        chatWorkers.remove(chatWorker);
    }

    @Override
    public void broadcast(String text) {
        chatWorkers.forEach(chatWorker -> chatWorker.send(text));
    }
}
