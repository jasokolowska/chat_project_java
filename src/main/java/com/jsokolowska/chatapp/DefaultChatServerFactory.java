package com.jsokolowska.chatapp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultChatServerFactory implements ChatServerFactory {

    private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(getClass().getName());

    @Override
    public ChatWorkers createChatWorkers() {
        return new SynchronizedChatWorkersProxy(new ListChatWorkers());
    }

    @Override
    public ExecutorService createExecutorService() {
        return Executors.newFixedThreadPool(1024);
    }

    @Override
    public Logger createLogger() {
        return logger::info;
    }

    @Override
    public ChatGroups createChatGroups() {
        return new ListChatGroups();
    }

}
