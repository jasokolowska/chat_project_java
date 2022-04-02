package com.jsokolowska.chatapp;

import lombok.extern.java.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Log
public class ChatServer {

    private final ChatServerFactory factory = new DefaultChatServerFactory();
    private final Logger logger = factory.createLogger();
    private final ChatWorkers chatWorkers = factory.createChatWorkers();
    private final ChatGroups chatGroups = factory.createChatGroups();
    private final ExecutorService executorService = factory.createExecutorService();

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        new ChatServer().start(port);
    }

    private void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            listen(serverSocket, port);
        } catch (IOException e) {
            logger.log("Server failed to start: " + e.getMessage());
        }
    }

    private void listen(ServerSocket serverSocket, int port) throws IOException {
        logger.log("Server is listening on port: " + port);
        ChatGroup chatGroup = new ChatGroup("GENERAL");

        while (true) {
            Socket socket = serverSocket.accept();
            logger.log("New connection established...");

            chatGroups.add(chatGroup);

            ChatWorker chatWorker = new ChatWorker(socket, chatWorkers, chatGroups);
            chatWorkers.add(chatWorker);
            log.info("chatWorkers: " + chatWorkers);

            chatGroup.addWorker(chatWorker);
            chatGroups.add(chatGroup);
            log.info("chatGroups.get(chatGroup.getName()).get().getWorkers(): " + chatGroups.get(chatGroup.getName()).get().getWorkers());

            executorService.execute(chatWorker);
        }
    }

}