package me.baldo3000.poool.model.utils;

public class CyclicBarrier {
    private final int parties;
    private int count = 0;
    private int generation = 0;

    public CyclicBarrier(int parties) {
        this.parties = parties;
    }

    public synchronized void await() {
        int myGeneration = generation;
        count++;
        if (count < parties) {
            while (generation == myGeneration) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        } else {
            count = 0;
            generation++;
            notifyAll();
        }
    }
}
