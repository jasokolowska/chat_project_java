package com.jsokolowska.chatapp;

import lombok.extern.java.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class ChatWorker implements Runnable {

    private static final String END_SESSION_COMMAND = "\\q";
    private static final String JOIN_COMMAND = "\\join";
    private static final String CREATE_NEW_GROUP_COMMAND = "\\create";

    private final Socket socket;
    private final ChatWorkers chatWorkers;
    private final ChatGroups chatGroups;
    private final MessageWriter writer;

    private ChatGroup currentGroup;

    public ChatWorker(Socket socket, ChatWorkers chatWorkers, ChatGroups chatGroups) {
        this.socket = socket;
        this.chatGroups = chatGroups;
        this.chatWorkers = chatWorkers;
        this.currentGroup = chatGroups.get("GENERAL").get();
        writer = new MessageWriter(socket);
    }

    @Override
    public void run() {
        writer.write("Your current room is: " + currentGroup.getName());
        new MessageReader(socket, this::onText, () -> chatWorkers.remove(this)).read();
    }

    private void onText(String text) {
        if (text.endsWith(END_SESSION_COMMAND)) {
            closeSocket();
        } else if (text.contains(CREATE_NEW_GROUP_COMMAND)) {
            String groupName = getGroupName(text);
            ChatGroup group = new ChatGroup(groupName);
            chatGroups.add(group);
            currentGroup = group;
            writer.cleanConsole();
            writer.write("Current room: " + currentGroup.getName());
        } else if (text.contains(JOIN_COMMAND)) {
            Optional<ChatGroup> group = chatGroups.get(getGroupName(text));
            if (chatGroups.get(getGroupName(text)).isPresent()) {
                group.get().addWorker(this);
                currentGroup = group.get();
                writer.cleanConsole();
                writer.write("Current room: " + currentGroup.getName());
            } else {
                writer.write("No such group exists");
            }

        }else {
            currentGroup.getWorkers().broadcast(text);
            log.info("currentGroup.getWorkers(): " + currentGroup.getWorkers());
//            chatWorkers.broadcast(text);
        }
    }

    public void send(String text) {
        writer.write(text);
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Closing socked failed: " + e.getMessage());
        }
    }

    private String getGroupName(String text) {
        int index = text.lastIndexOf(" ");
        String groupName = text.substring(index + 1);
        return groupName;
    }
}
