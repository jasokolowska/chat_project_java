package com.jsokolowska.chatapp.server;

import com.jsokolowska.chatapp.server.messages.ChatMessages;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.io.*;
import java.util.logging.Level;

@Log
@NoArgsConstructor
public class ChatHistory {

    private static final String FILE_NAME = "ChatMessages.o";
    private ChatMessages messages;

    public ChatHistory(ChatMessages messages) {
        this.messages = messages;
    }

    public void save() {
        while (true) {
            exportData(messages);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportData(ChatMessages messages) {
        try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(messages);
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "File not found: " + e.getMessage());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Creating output stream failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ChatMessages importData() {
        try (FileInputStream fis = new FileInputStream(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            return (ChatMessages) ois.readObject();
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "File not found: " + e.getMessage());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Creating output stream failed: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE, "Incorrect data type in file: " + FILE_NAME);
        }
        return new ChatMessages();
    }
}
