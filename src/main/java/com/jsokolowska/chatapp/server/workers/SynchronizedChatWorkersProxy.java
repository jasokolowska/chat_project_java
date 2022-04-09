package com.jsokolowska.chatapp.server.workers;

import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SynchronizedChatWorkersProxy implements ChatWorkers {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ChatWorkers chatWorkers;

    public SynchronizedChatWorkersProxy(ChatWorkers chatWorkers) {
        this.chatWorkers = chatWorkers;
    }

    @Override
    public void add(ChatWorker chatWorker) {
        lock.writeLock().lock();
        chatWorkers.add(chatWorker);
        lock.writeLock().unlock();
    }

    @Override
    public void remove(ChatWorker chatWorker) {
        lock.writeLock().lock();
        chatWorkers.remove(chatWorker);
        lock.writeLock().unlock();
    }

    @Override
    public void broadcast(String text, String group) {
        lock.readLock().lock();
        chatWorkers.broadcast(text, group);
        lock.readLock().unlock();
    }

    @Override
    public Set<ChatWorker> getAll() {
        return chatWorkers.getAll();
    }
}
