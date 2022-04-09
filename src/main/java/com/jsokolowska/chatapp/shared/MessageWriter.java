package com.jsokolowska.chatapp.shared;

import com.jsokolowska.chatapp.server.messages.ChatMessage;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;

@Log
public class MessageWriter {

    private ObjectOutputStream object;

    public MessageWriter(Socket socket) {
        try {
            OutputStream output = socket.getOutputStream();
            object = new ObjectOutputStream(output);
        } catch (IOException e) {
           log.log(Level.SEVERE, "Creating output stream failed: " + e.getMessage());
        }
    }

    public void write(ChatMessage message) {
        try {
            object.writeObject(message);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Creating output stream failed: " + e.getMessage());
        }
    }

}
