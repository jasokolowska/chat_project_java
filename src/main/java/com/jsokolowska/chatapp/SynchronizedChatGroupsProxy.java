package com.jsokolowska.chatapp;

import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SynchronizedChatGroupsProxy implements ChatGroups {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ChatGroups chatGroups;

    public SynchronizedChatGroupsProxy(ChatGroups chatGroups) {
        this.chatGroups = chatGroups;
    }

    @Override
    public void add(ChatGroup chatGroup) {
        lock.writeLock().lock();
        chatGroups.add(chatGroup);
        lock.writeLock().unlock();
    }

    @Override
    public void remove(ChatGroup chatGroup) {
        lock.writeLock().lock();
        chatGroups.remove(chatGroup);
        lock.writeLock().unlock();
    }

    @Override
    public Optional<ChatGroup> get(String name) {
        lock.readLock().lock();
        Optional<ChatGroup> chatGroup = chatGroups.get(name);
        lock.readLock().unlock();
        return chatGroup;
    }
}
