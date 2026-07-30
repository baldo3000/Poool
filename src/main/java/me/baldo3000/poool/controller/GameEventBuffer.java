package me.baldo3000.poool.controller;

import me.baldo3000.poool.model.utils.V2d;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameEventBuffer {
    private final Lock mutex = new ReentrantLock();
    private V2d lastPlayerDirection;
    private V2d lastCpuDirection;

    public void onPlayerInput(V2d direction) {
        try {
            mutex.lock();
            lastPlayerDirection = direction;
        } finally {
            mutex.unlock();
        }
    }

    public void onCpuInput(V2d direction) {
        try {
            mutex.lock();
            lastCpuDirection = direction;
        } finally {
            mutex.unlock();
        }
    }

    public Optional<V2d> getLastPlayerDirection() {
        try {
            mutex.lock();
            var tmp = lastPlayerDirection;
            lastPlayerDirection = null;
            return Optional.ofNullable(tmp);
        } finally {
            mutex.unlock();
        }
    }

    public Optional<V2d> getLastCpuDirection() {
        try {
            mutex.lock();
            var tmp = lastCpuDirection;
            lastCpuDirection = null;
            return Optional.ofNullable(tmp);
        } finally {
            mutex.unlock();
        }
    }
}
