package com.training.multicommunication;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.List;

@Log
public class WorkerThread implements Runnable{
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private List<WorkerThread> workers;
    private String userName;

    public WorkerThread(Socket socket, List<WorkerThread> workers) throws IOException {
        this.socket = socket;
        this.workers = workers;
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @SneakyThrows
    @Override
    public void run() {
        String userName = reader.readLine();
        String serverMessage = ("New user connected to the server: " + userName);
        broadcast(serverMessage);

        boolean quit = false;
        while (!quit) {
            String request = reader.readLine();

            if (request.equals("quit")) {
                quit = true;
                writer.println("Goodbye");
            } else if (request.contains("date")) {
                writer.println("Today is: " + new Date());
            }  else {
                serverMessage = "[" + userName + "]: " + request;
                broadcast(serverMessage);
            }
        }
    }

    private void broadcast(String serverMessage) {
        workers.stream()
                .filter(worker -> !worker.equals(this))
                .forEach(worker -> worker.writer.println(serverMessage));
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
