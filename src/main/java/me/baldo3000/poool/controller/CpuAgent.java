package me.baldo3000.poool.controller;


import me.baldo3000.poool.model.utils.V2d;

import java.util.Random;

public class CpuAgent extends Thread {
    private final GameEventListener listener;
    private final Random random = new Random(42);
    private volatile boolean running = true;

    public CpuAgent(GameEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(random.nextInt(500));
                V2d impulse = new V2d(random.nextDouble() - 0.5, random.nextDouble() - 0.5);
                //V2d impulse = new V2d(0.0, 0.0);
                listener.notifyCpuInput(impulse);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    public void stopCpu() {
        this.running = false;
    }
}