package com.jsokolowska.chatapp;

import lombok.Data;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Level;

@Data
@Log
public class ChatWorker implements Runnable{

    private static final String END_SESSION_COMMAND = "QUIT";
    private static final String JOIN_COMMAND = "JOIN";
    private static final String CREATE_NEW_GROUP_COMMAND = "CREATE";
    private static final String SWITCH_GROUP_COMMAND = "SWITCH";

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
        send("Your current room is: " + currentGroup.getName());
        new MessageReader(socket, this::onText, () -> chatWorkers.remove(this)).readMessage();
    }

    private void onText(String text) {
        if (text.endsWith(END_SESSION_COMMAND)) {
            closeSocket();
        } else if (text.contains(CREATE_NEW_GROUP_COMMAND)) {
            String groupName = getGroupName(text);
            ChatGroup group = new ChatGroup(groupName);
            group.addWorker(this);
            chatGroups.add(group);
            currentGroup = group;
            send(">>>>> Current room: " + currentGroup.getName() + " <<<<<");
        } else if (text.contains(JOIN_COMMAND)) {
            Optional<ChatGroup> group = chatGroups.get(getGroupName(text));
            if (chatGroups.get(getGroupName(text)).isPresent()) {
                group.get().addWorker(this);
                currentGroup = group.get();;
                send(">>>>> Current room: " + currentGroup.getName() + " <<<<<");
            } else {
                send("No such group exists");
            }
        } else if(text.contains(SWITCH_GROUP_COMMAND)) {
            Optional<ChatGroup> group = chatGroups.get(getGroupName(text));
            currentGroup = group.get();
            send(">>>>> Current room: " + currentGroup.getName() + " <<<<<");
        }
        else {
            ChatMessage message = new ChatMessage(text, currentGroup.getName());
            currentGroup.getMessages().add(message);
            currentGroup.getWorkers().broadcast(message.getContent(), currentGroup.getName());
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

    private String getGroupName(String text) {
        int index = text.lastIndexOf(" ");
        String groupName = text.substring(index + 1);
        return groupName;
    }
}
