package com.training.multicommunication;


import lombok.extern.java.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log
public class Server {

    private static final int PORT = 9091;
    private static List<WorkerThread> workers = new ArrayList<>();
    private static ExecutorService executor = Executors.newFixedThreadPool(1024);

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        log.info("Server is listening ...");

        try {
            while(true) {
                Socket client = server.accept();
                WorkerThread worker = new WorkerThread(client, workers);
                log.info("New connection established");
                workers.add(worker);

                executor.submit(worker);
            }
        } finally {
            server.close();
        }
    }
}
