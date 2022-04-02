package com.training.singlecommunication;

import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Log
public class Client {

    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 9090;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(IP_ADDRESS, PORT);

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter sender = new PrintWriter(socket.getOutputStream(), true);

        try {
            while(true) {
                System.out.println(reader.readLine());
                System.out.println(">");
                String request = keyboard.readLine();
                sender.println(request);
                if (request.equals("quit")) {
                    System.out.println(reader.readLine());
                    break;
                }
                log.info("Request is sent the the server: " + request);
            }
        } finally {
            socket.close();
        }
    }
}
