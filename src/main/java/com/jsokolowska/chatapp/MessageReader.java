package com.jsokolowska.chatapp;

import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log
public class MessageReader {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final Consumer<String> onText;
    private ObjectInputStream inputObject;
    private BufferedReader reader;
    private Runnable onClose;
    private FileSender fileSender;
    private FileReceiver fileReceiver;

    public MessageReader(Socket socket, InputStream inputStream, Consumer<String> onText) {
        this.onText = onText;
        reader = new BufferedReader(new InputStreamReader(inputStream));
        fileSender = new FileSender(socket);
        fileReceiver = new FileReceiver(socket);
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
                if(text.contains("UPLOAD")) {
                    log.info("Sending text to the server");
                    onText.accept(text);
                    log.info("Sending file to the server...");
                    fileSender.send("C:\\Development\\Lufthansa\\chat_project_java\\src\\main\\resources\\server_test_file.txt");
                    log.info("File sended successfully...");
                    onText.accept("Sending a file...");
                } else {
                    onText.accept(text);
                }
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
//                if (message.getContent().contains("DOWNLOAD")) {
//                    log.info("Sending message DOWNLOAD to the server");
//                    onText.accept(message.getContent());
//                    log.info("Downloading file from the server..");
//                    fileReceiver.receive("new_file_received_by_client");
//                    log.info("File received succesfully");
//                } else {
//                    onText.accept(message.getContent());
//                }
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
