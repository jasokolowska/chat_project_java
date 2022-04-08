package com.jsokolowska.chatapp;

import com.sun.source.tree.SwitchExpressionTree;

import java.util.Set;

public interface ChatWorkers {

    void add(ChatWorker chatWorker);

    void remove(ChatWorker chatWorker);

    void broadcast(String text, String groupName);

    Set<ChatWorker> getAll();
}
