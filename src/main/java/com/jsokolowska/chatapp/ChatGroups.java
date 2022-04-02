package com.jsokolowska.chatapp;

import java.util.Optional;

public interface ChatGroups {

    void add(ChatGroup chatGroup);

    void remove(ChatGroup chatGroup);

    Optional<ChatGroup> get(String name);
}
