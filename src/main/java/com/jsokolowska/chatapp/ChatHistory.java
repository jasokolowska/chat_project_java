package com.jsokolowska.chatapp;

import lombok.extern.java.Log;

import java.io.*;
import java.util.logging.Level;

@Log
public class ChatHistory {

    private static final String FILE_NAME = "ChatMessages.o";

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
            log.log(Level.SEVERE, "Creating outpus stream failed: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE, "Niezgodny typ danych w pliku: " + FILE_NAME);
        }
        return new ChatMessages();
    }
}
