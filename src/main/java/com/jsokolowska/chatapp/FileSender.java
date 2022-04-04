package com.jsokolowska.chatapp;

import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

@Log
public class FileSender {

    //reading file from system and send it to the socket

    private DataInputStream dataInput;
    private DataOutputStream dataOutput;

    public FileSender(Socket socket) {
        try {
            this.dataInput = new DataInputStream(socket.getInputStream());
            this.dataOutput = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Creating input/output stream failed: " + e.getMessage());
        }
    }

    public void send(String path) throws IOException {
        int bytes = 0;
        var file = new File(path);
        var fileInputStream = new FileInputStream(file);
        dataOutput.writeLong(file.length());
        byte[] buffer = new byte[4*1024];
        while((bytes = fileInputStream.read(buffer)) != -1) {
            dataOutput.write(buffer, 0, bytes);
            dataOutput.flush();
        }
        fileInputStream.close();
    }
}
