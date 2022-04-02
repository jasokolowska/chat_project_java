package com.training.multicommunication;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Log
public class ServerReader implements Runnable{
    private Socket socket;
    private BufferedReader reader;
    private Client client;

    public ServerReader(Socket socket, Client client) throws IOException {
        this.socket = socket;
        this.client = client;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @SneakyThrows
    @Override
    public void run() {
        try {
            while (true) {
                String response = reader.readLine();
                System.out.println("\n" + response);

                if(client.getUserName() != null) {
                    System.out.println("[" + client.getUserName() + "]: ");
                }
            }
        } catch (IOException e) {
            log.info("Message from the server is not readable");
        } finally {
            socket.close();
        }
    }
}
