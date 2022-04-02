package com.training.multicommunication;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Log
public class ServerWriter implements Runnable{
    private final Socket socket;
    private final PrintWriter writer;
    private final Client client;

    public ServerWriter(Socket socket, Client client) throws IOException {
        this.socket = socket;
        this.client = client;
        this.writer = new PrintWriter(socket.getOutputStream(), true);
    }

    @SneakyThrows
    @Override
    public void run() {
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        boolean quit = false;

        try {
            System.out.print("Enter your name: ");
            String userName = keyboard.readLine();
            writer.println(userName);
            client.setUserName(userName);

            while(!quit) {
                String request = keyboard.readLine();
                writer.println(request);
                if (request.equals("quit")) {
                    quit = true;
                }
                log.info("Request is sent the the server: " + request);
            }
        } catch (IOException e) {
            e.getMessage();
        } finally{
            socket.close();
        }
    }
}
