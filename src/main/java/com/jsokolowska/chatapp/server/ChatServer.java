package com.jsokolowska.chatapp.server;

import com.jsokolowska.chatapp.server.groups.ChatGroup;
import com.jsokolowska.chatapp.server.groups.ChatGroups;
import com.jsokolowska.chatapp.server.messages.ChatMessage;
import com.jsokolowska.chatapp.server.messages.ChatMessages;
import com.jsokolowska.chatapp.server.workers.ChatWorker;
import com.jsokolowska.chatapp.server.workers.ChatWorkers;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Log
public class ChatServer {

    private final ChatServerFactory factory = new DefaultChatServerFactory();
    private final ChatWorkers chatWorkers = factory.createChatWorkers();
    private final ChatGroups chatGroups = factory.createChatGroups();
    private final ExecutorService executorService = factory.createExecutorService();
    private final ChatHistory history = new ChatHistory();
    private static ChatMessages messages = new ChatMessages();

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        new ChatServer().start(port);
    }

    private void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            loadHistory();
            Thread historyRecord = new Thread(() -> new ChatHistory(messages).save());
            historyRecord.setDaemon(true);
            historyRecord.start();
            listen(serverSocket, port);
        } catch (IOException e) {
            log.info("Server failed to start: " + e.getMessage());
        }
    }

    private void listen(ServerSocket serverSocket, int port) throws IOException {
        log.info("Server is listening on port: " + port);

        if (chatGroups.get("GENERAL").isEmpty()) {
            chatGroups.add(new ChatGroup("GENERAL"));
        }
        ChatGroup chatGroup = chatGroups.get("GENERAL").get();

        while (true) {
            Socket socket = serverSocket.accept();
            log.info("New connection established...");

            ChatWorker chatWorker = new ChatWorker(socket, chatWorkers, chatGroups, messages);
            chatWorkers.add(chatWorker);

            chatGroup.addWorker(chatWorker);
            
            executorService.execute(chatWorker);
        }
    }

    public void loadHistory(){
        log.info("Load history started...");
        messages = history.importData();
        log.info("Messages loaded successfully");
        for (ChatMessage message : messages.getAll()) {
            String groupName = message.getGroupName();
            if (chatGroups.get(groupName).isEmpty()) {
                chatGroups.add(new ChatGroup(groupName));
            }
            chatGroups.get(groupName).get().getMessages().add(message);
            chatGroups.get(groupName).get().getUserNames().add(message.getUserName());
        }
    }
}
