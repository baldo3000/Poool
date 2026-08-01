package me.baldo3000.poool.model.boardupdate;

import me.baldo3000.poool.model.Ball;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class BallAllocator {

    private final Set<Ball> lockedBalls = Collections.newSetFromMap(new IdentityHashMap<>());

    public synchronized void acquirePair(Ball a, Ball b) throws InterruptedException {
        while (lockedBalls.contains(a) || lockedBalls.contains(b)) {
            wait();
        }
        lockedBalls.add(a);
        lockedBalls.add(b);
    }

    public synchronized void releasePair(Ball a, Ball b) {
        lockedBalls.remove(a);
        lockedBalls.remove(b);
        notifyAll();
    }
}
