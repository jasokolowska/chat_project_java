package com.jsokolowska.chatapp;

import java.util.concurrent.ExecutorService;

public interface ChatServerFactory {

    ChatWorkers createChatWorkers();

    ExecutorService createExecutorService();

    Logger createLogger();

    ChatGroups createChatGroups();

}
