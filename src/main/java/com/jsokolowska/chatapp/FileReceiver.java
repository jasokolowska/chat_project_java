package com.jsokolowska.chatapp;

import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

@Log
public class FileReceiver implements Runnable{

    //receiving file from the socket and writing on disk

    private DataInputStream dataInput;

    public FileReceiver(Socket socket) {
        try {
            this.dataInput = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Creating input/output stream failed: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while(true) {
                if (dataInput.readLong() > 0) {
                    receive("testfile.txt");
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Reading input stream failed: " + e.getMessage());
        }
    }

    public void receive(String fileName) throws IOException {
        log.info("Method receive file running...");
        int bytes = 0;
        var fileOutputStream = new FileOutputStream(fileName);
        log.info("fILEoUTPUTsTREAM OBJECT CREATED");
        long size = dataInput.readLong();
        log.info("dataInput.readLong()");

        byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = dataInput.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer, 0, bytes);
            size -= bytes;
        }
        log.info("Method receive file stopped BEFORE CLOSING SOCKET");
        fileOutputStream.close();
        log.info("Method receive file stopped...");
    }
}
