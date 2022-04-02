package com.training.multicommunication;

import lombok.Data;
import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;

@Data
@Log
public class Client {

    private final String hostname;
    private final int port;
    private String userName;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void start() {
        try {
            Socket socket = new Socket(hostname, port);
            log.info("Connected to the chat server");

            new Thread(new ServerWriter(socket, this)).start();
            new Thread(new ServerReader(socket, this)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        Client client = new Client(hostname, port);
        client.start();
    }
}
