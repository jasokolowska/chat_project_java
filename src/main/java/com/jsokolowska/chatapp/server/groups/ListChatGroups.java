package com.jsokolowska.chatapp.server.groups;

import java.io.Serializable;
import java.util.*;

public class ListChatGroups implements ChatGroups, Serializable {

    private final Set<ChatGroup> chatGroups = new HashSet<>();

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

    @Override
    public Set<ChatGroup> getAll() {
        return chatGroups;
    }
}
