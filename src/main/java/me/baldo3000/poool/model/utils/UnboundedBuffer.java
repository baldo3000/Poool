package me.baldo3000.poool.model.utils;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UnboundedBuffer<Item> {

    private final LinkedList<Item> buffer;
    private final Lock lock;
    private final Condition notEmpty;

    public UnboundedBuffer() {
        buffer = new LinkedList<>();
        lock = new ReentrantLock();
        notEmpty = lock.newCondition();
    }

    public void put(Item item) {
        try {
            lock.lock();
            buffer.addLast(item);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public Item get() throws InterruptedException {
        try {
            lock.lock();
            while (buffer.isEmpty()) {
                notEmpty.await();
            }
            return buffer.removeFirst();
        } finally {
            lock.unlock();
        }
    }
}
