package com.training.singlecommunication;


import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

@Log
public class ServerDate {

    private static final int PORT = 9090;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        log.info("Server is listening ...");

        Socket socket = server.accept();
        log.info("Server accepted connection ...");

        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        writer.println("Sending message to the client on the beginning of the connection");
        log.info("Server sent message to the client ..." + new Date());

        Boolean quit = false;

        while(!quit) {
            String response = reader.readLine();

            if (response.contains("date")) {
                log.info("After receiving message from the client server is sending response...");
                writer.println("This is current date: " + new Date());
            } else if (response.equals("quit")) {
                quit = true;
                writer.println("Goodbye...");
            } else {
                writer.println("This is response from server");
            }
        }

        socket.close();
        server.close();
    }
}
