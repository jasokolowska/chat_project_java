package com.jsokolowska.chatapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListChatGroups implements ChatGroups, Serializable {

    private final List<ChatGroup> chatGroups = new ArrayList<>();

    @Override
    public void add(ChatGroup chatGroup) {
        chatGroups.add(chatGroup);
    }

    @Override
    public void remove(ChatGroup chatGroup) {
        chatGroups.remove(chatGroup);
    }

    @Override
    public Optional<ChatGroup> get(String name) {
        return chatGroups.stream()
                .filter(group -> group.getName().equals(name))
                .findFirst();
    }

    public void createNew(String groupName) {
        add(new ChatGroup(groupName));
    }


}
