package com.jsokolowska.chatapp.server.workers;

import java.util.Set;

public interface ChatWorkers {

    void add(ChatWorker chatWorker);

    void remove(ChatWorker chatWorker);

    void broadcast(String text, String groupName);

    Set<ChatWorker> getAll();
}
