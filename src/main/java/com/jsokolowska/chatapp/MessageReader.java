package com.jsokolowska.chatapp;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageReader {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final Consumer<String> onText;
    private ObjectInputStream inputObject;
    private BufferedReader reader;
    private Runnable onClose;

    public MessageReader(InputStream inputStream, Consumer<String> onText) {
        this.onText = onText;
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public MessageReader(Socket socket, Consumer<String> onText, Runnable onClose) {
        this.onText = onText;
        this.onClose = onClose;
        try {
            inputObject = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Creating input stream failed: " + e.getMessage());
        }
    }

    public void read() {
        String text;
        try {
            while ((text = reader.readLine()) != null) {
                onText.accept(text);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "read(): Read message failed: " + e.getMessage());
        }
        finally {
            if (onClose != null) {
                onClose.run();
            }
        }
    }

    public void readMessage() {
        ChatMessage message;
        try {
            while ((message = (ChatMessage) inputObject.readObject()) != null) {
                onText.accept(message.getContent());
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "readMessage(): Read message failed: " + e.getMessage());
        }
        finally {
            if (onClose != null) {
                onClose.run();
            }
        }
    }
}
