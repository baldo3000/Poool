package me.baldo3000.poool.model.utils;

import java.util.LinkedList;

public class UnboundedBuffer<Item> {

    private LinkedList<Item> buffer;
    private int maxSize;

    public UnboundedBuffer() {
        buffer = new LinkedList<Item>();
    }

    public synchronized void put(Item item) throws InterruptedException {
        buffer.addLast(item);
        notifyAll();
    }

    public synchronized Item get() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }
        Item item = buffer.removeFirst();
        notifyAll();
        return item;
    }

    private boolean isEmpty() {
        return buffer.isEmpty();
    }

    /*private final LinkedList<Item> buffer;
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
    }*/
}
