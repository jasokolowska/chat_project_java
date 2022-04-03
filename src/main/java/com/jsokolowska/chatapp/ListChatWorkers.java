package com.jsokolowska.chatapp;

import java.util.HashSet;
import java.util.Set;

public class ListChatWorkers implements ChatWorkers {

    private final Set<ChatWorker> chatWorkers = new HashSet<>();

    @Override
    public void add(ChatWorker chatWorker) {
        chatWorkers.add(chatWorker);
    }

    @Override
    public void remove(ChatWorker chatWorker) {
        chatWorkers.remove(chatWorker);
    }

    @Override
    public void broadcast(String text, String groupName) {
        chatWorkers.stream()
                .filter(chatWorker -> chatWorker.getCurrentGroup().getName().equals(groupName))
                .forEach(chatWorker -> chatWorker.send(text));
    }
}
