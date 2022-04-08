package com.jsokolowska.chatapp;

import lombok.extern.java.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

@Log
public class ChatClient {

    private final Consumer<String> onText;
    private final Runnable readFromSocket;
    private final Runnable readFromConsole;
    private final MessageWriter writer;
    private final String userName;

    public ChatClient(String host, int port, String name) throws IOException {
        Socket socket = new Socket(host, port);
        this.writer = new MessageWriter(socket);
        this.userName = name;
        onText = text -> writer.write(new ChatMessage("[" + name + "]: " + text));
        readFromSocket = () -> new MessageReader(socket, System.out::println, () -> {}).readMessage();
        readFromConsole = () -> new MessageReader(socket, System.in, onText).read();
    }

    public static void main(String[] args) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String name = args[2];
        new ChatClient(host, port, name).start();
    }

    private void start() {
        writer.write(new ChatMessage("USERNAME: " + userName));
        new Thread(readFromSocket).start();
        log.info("Connected to the chat server");
        Thread consoleMessageReader = new Thread(readFromConsole);
        consoleMessageReader.setDaemon(true);
        consoleMessageReader.start();
    }

}
