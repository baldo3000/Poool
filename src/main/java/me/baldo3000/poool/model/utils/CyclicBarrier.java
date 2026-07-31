package me.baldo3000.poool.model.utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier {
    private final int parties;
    private int count;
    private int generation = 0;

    private final Lock lock = new ReentrantLock();
    private final Condition trip = lock.newCondition();

    public CyclicBarrier(int parties) {
        this.parties = parties;
        this.count = parties;
    }

    public void await() {
        try {
            lock.lock();
            int gen = generation;
            count--;

            if (count == 0) {
                generation++;
                count = parties;
                trip.signalAll();
            } else {
                while (gen == generation) {
                    try {
                        trip.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
