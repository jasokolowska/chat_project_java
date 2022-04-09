package com.jsokolowska.chatapp.server;

import com.jsokolowska.chatapp.server.groups.ChatGroups;
import com.jsokolowska.chatapp.server.workers.ChatWorkers;

import java.util.concurrent.ExecutorService;

public interface ChatServerFactory {

    ChatWorkers createChatWorkers();

    ExecutorService createExecutorService();

    ChatGroups createChatGroups();

}
