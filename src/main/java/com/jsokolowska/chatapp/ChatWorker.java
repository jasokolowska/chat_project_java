package com.jsokolowska.chatapp;

import com.sun.jdi.PrimitiveValue;
import lombok.Data;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Data
@Log
public class ChatWorker implements Runnable{

    private static final String END_SESSION_COMMAND = "QUIT";
    private static final String JOIN_COMMAND = "JOIN";
    private static final String CREATE_NEW_GROUP_COMMAND = "CREATE";
    private static final String SWITCH_GROUP_COMMAND = "SWITCH";
    private static final String UPLOAD_COMMAND = "UPLOAD";
    private static final CharSequence DOWNLOAD_COMMAND = "DOWNLOAD";

    private final Socket socket;
    private final ChatWorkers chatWorkers;
    private final ChatGroups chatGroups;
    private final MessageWriter writer;
    private final FileSender fileSender;
    private final FileReceiver fileReceiver;
    private final MessageReader reader;
    private String tempFilePath;
    private ChatGroup currentGroup;

    public ChatWorker(Socket socket, ChatWorkers chatWorkers, ChatGroups chatGroups) {
        this.socket = socket;
        this.chatGroups = chatGroups;
        this.chatWorkers = chatWorkers;
        this.currentGroup = chatGroups.get("GENERAL").get();
        this.fileSender = new FileSender(socket);
        this.fileReceiver = new FileReceiver(socket);
        this.writer = new MessageWriter(socket);
        this.reader = new MessageReader(socket, this::onText, () -> chatWorkers.remove(this));
    }

    @Override
    public void run() {
        send("Your current room is: " + currentGroup.getName());
        currentGroup.getMessages().forEach(message -> send(message.getContent()));
        reader.readMessage();
    }

    private void onText(String text) {
        if (text.endsWith(END_SESSION_COMMAND)) {
            send("Goodbye");
            closeSocket();
        } else if (text.contains(CREATE_NEW_GROUP_COMMAND)) {
            String groupName = getVariable(text);
            ChatGroup group = new ChatGroup(groupName);
            group.addWorker(this);
            chatGroups.add(group);
            currentGroup = group;
            send(">>>>> Current room: " + currentGroup.getName() + " <<<<<");
        } else if (text.contains(JOIN_COMMAND)) {
            Optional<ChatGroup> group = chatGroups.get(getVariable(text));
            if (chatGroups.get(getVariable(text)).isPresent()) {
                group.get().addWorker(this);
                currentGroup = group.get();;
                send(">>>>> Current room: " + currentGroup.getName() + " <<<<<");
            } else {
                send("No such group exists");
            }
        } else if(text.contains(SWITCH_GROUP_COMMAND)) {
            Optional<ChatGroup> group = chatGroups.get(getVariable(text));
            currentGroup = group.get();
            send(">>>>> Current room: " + currentGroup.getName() + " <<<<<");
        } else if (text.contains(UPLOAD_COMMAND)) {
            try {
                String tempFile = "temporary_server_file.txt";
                fileReceiver.receive(tempFile);
                log.info("File received successfully by server");
                tempFilePath = "C:\\Development\\Lufthansa\\chat_project_java\\" + tempFile;
                currentGroup.getWorkers().broadcast("Do you want to download a file? (Y - type DOWNLOAD)", currentGroup.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (text.contains(DOWNLOAD_COMMAND)) {
            try {
                log.info("Start sending file from server to the client");
                fileSender.send("C:\\Development\\Lufthansa\\chat_project_java\\temporary_server_file.txt");
                log.info("File send succesfully to the client");
                send("File downloaded successfully");
            } catch (IOException e) {
                log.log(Level.SEVERE, "Sending file failed: " + e.getMessage());
            }
        } else {
            ChatMessage message = new ChatMessage(text, currentGroup.getName());
            ChatServer.save(message);
            currentGroup.getMessages().add(message);
            currentGroup.getWorkers().broadcast(message.getContent(), currentGroup.getName());
        }
    }

    private void uploadFile(String path) {
        try {
            fileSender.send(path);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Sending file failed: " + e.getMessage());
        }
    }

    public void send(String text) {
        writer.write(new ChatMessage(text, currentGroup.getName()));
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Closing socked failed: " + e.getMessage());
        }
    }

    private String getVariable(String text) {
        int index = text.lastIndexOf(" ");
        return text.substring(index + 1);
    }
}
