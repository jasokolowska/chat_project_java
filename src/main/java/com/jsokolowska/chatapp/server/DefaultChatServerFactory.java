package com.jsokolowska.chatapp.server;

import com.jsokolowska.chatapp.server.groups.ChatGroups;
import com.jsokolowska.chatapp.server.groups.ListChatGroups;
import com.jsokolowska.chatapp.server.workers.ChatWorkers;
import com.jsokolowska.chatapp.server.workers.ListChatWorkers;
import com.jsokolowska.chatapp.server.workers.SynchronizedChatWorkersProxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultChatServerFactory implements ChatServerFactory {

    @Override
    public ChatWorkers createChatWorkers() {
        return new SynchronizedChatWorkersProxy(new ListChatWorkers());
    }

    @Override
    public ExecutorService createExecutorService() {
        return Executors.newFixedThreadPool(1024);
    }

    @Override
    public ChatGroups createChatGroups() {
        return new ListChatGroups();
    }

}
