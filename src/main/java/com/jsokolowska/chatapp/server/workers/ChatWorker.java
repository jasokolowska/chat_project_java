package com.jsokolowska.chatapp.server.workers;

import com.jsokolowska.chatapp.server.messages.ChatMessage;
import com.jsokolowska.chatapp.server.messages.ChatMessages;
import com.jsokolowska.chatapp.shared.FileReceiver;
import com.jsokolowska.chatapp.shared.FileSender;
import com.jsokolowska.chatapp.server.groups.ChatGroup;
import com.jsokolowska.chatapp.server.groups.ChatGroups;
import com.jsokolowska.chatapp.shared.MessageReader;
import com.jsokolowska.chatapp.shared.MessageWriter;
import lombok.Data;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

@Data
@Log
public class ChatWorker implements Runnable {

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
    private String tempFilePath;
    private ChatGroup currentGroup;
    private ChatMessages messages;
    private String userName;

    public ChatWorker(Socket socket, ChatWorkers chatWorkers, ChatGroups chatGroups, ChatMessages messages) {
        this.socket = socket;
        this.chatGroups = chatGroups;
        this.chatWorkers = chatWorkers;
        this.messages = messages;
        this.currentGroup = chatGroups.get("GENERAL").get();
        this.fileSender = new FileSender(socket);
        this.fileReceiver = new FileReceiver(socket);
        this.writer = new MessageWriter(socket);

    }

    @Override
    public void run() {
        send("Your current room is: " + currentGroup.getName());
//        send("Users: " + currentGroup.getUserNames());
//        currentGroup.getMessages().forEach(message -> send(message.getContent()));
        new MessageReader(socket, this::onText, () -> chatWorkers.remove(this)).readMessage();
    }

    private void onText(String text) {
        if (text.endsWith(END_SESSION_COMMAND)) {
            send("Goodbye");
            closeSocket();
        } else if (text.startsWith("USERNAME:")) {
            addUserName(text);
            send("Users: " + currentGroup.getUserNames());
            currentGroup.getMessages().forEach(message -> send(message.getContent()));
        } else if (text.contains(CREATE_NEW_GROUP_COMMAND)) {
            createNewGroup(text);
        } else if (text.contains(JOIN_COMMAND)) {
            joinExistingGroup(text);
        } else if (text.contains(SWITCH_GROUP_COMMAND)) {
            switchGroup(getVariable(text));
        } else if (text.contains(UPLOAD_COMMAND)) {
            fileUpload();
        } else if (text.contains(DOWNLOAD_COMMAND)) {
            fileDownload();
        } else {
            ChatMessage message = new ChatMessage(text, currentGroup.getName(), userName);
            messages.save(message);
            currentGroup.getMessages().add(message);
            currentGroup.getWorkers().broadcast(message.getContent(), currentGroup.getName());
        }
    }

    private void addUserName(String text) {
        this.userName = getVariable(text);
        currentGroup.getUserNames().add(userName);
        chatGroups.getAll().stream()
                .filter(group -> !group.getName().equals(currentGroup.getName()))
                .filter(group -> group.getUserNames().contains(userName))
                .forEach(group -> group.addWorker(this));
    }

    public void send(String text) {
        writer.write(new ChatMessage(text, currentGroup.getName(), this.userName));
    }

    private void createNewGroup(String text) {
        String groupName = getVariable(text);
        ChatGroup group = new ChatGroup(groupName);
        group.addWorker(this);
        chatGroups.add(group);
        currentGroup = group;
        send(">>>>> Current room: " + currentGroup.getName() + " <<<<<");
    }

    private void switchGroup(String groupName) {
        Optional<ChatGroup> group = chatGroups.get(groupName);
        if (group.isPresent()) {
            if (group.get().getUserNames().contains(userName)) {
                currentGroup = group.get();
                send(">>>>> Current room: " + currentGroup.getName() + " <<<<<");
                currentGroup.getMessages().forEach(msg -> send(msg.getContent()));
            } else {
                send("You don't have access to this group, try JOIN to ask for access");
            }
        } else {
            send("No such group exists");
        }

    }

    private void joinExistingGroup(String text) {
        Optional<ChatGroup> group = chatGroups.get(getVariable(text));
        if (group.isPresent()) {
            if (group.get().getUserNames().contains(userName)) {
                switchGroup(getVariable(text));
            } else {
                System.out.println("User names: " + group.get().getUserNames());
                System.out.println(userName);
                group.get().addWorker(this);
                currentGroup = group.get();
                send(">>>>> Current room: " + currentGroup.getName() + " <<<<<");
                currentGroup.getMessages().forEach(msg -> send(msg.getContent()));
            }
        } else {
            send("No such group exists");
        }
    }

    private void fileDownload() {
        try {
            log.info("Start sending file from server to the client");
            fileSender.send("C:\\Development\\Lufthansa\\chat_project_java\\temporary_server_file.txt");
            log.info("File send succesfully to the client");
            send("File downloaded successfully");
        } catch (IOException e) {
            log.log(Level.SEVERE, "Sending file failed: " + e.getMessage());
        }
    }

    private void fileUpload() {
        try {
            String tempFile = "temporary_server_file.txt";
            fileReceiver.receive(tempFile);
            log.info("File received successfully by server");
            tempFilePath = "C:\\Development\\Lufthansa\\chat_project_java\\" + tempFile;
            currentGroup.getWorkers().broadcast("Do you want to download a file? (Y - type DOWNLOAD)", currentGroup.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatWorker that = (ChatWorker) o;

        return Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return userName != null ? userName.hashCode() : 0;
    }
}
