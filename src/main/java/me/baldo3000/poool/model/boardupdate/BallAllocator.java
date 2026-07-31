package me.baldo3000.poool.model.boardupdate;

import me.baldo3000.poool.model.Ball;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class BallAllocator {

    private final Map<Ball, ReentrantLock> entries;

    /**
     * @param balls the fixed set of balls in the simulation. Each gets its own
     *              lock, assigned once at construction time.
     */
    public BallAllocator(List<Ball> balls) {
        Map<Ball, ReentrantLock> map = new IdentityHashMap<>(balls.size() * 2);
        int order = 0;
        for (Ball b : balls) {
            map.put(b, new ReentrantLock());
        }
        this.entries = map;
    }

    public void acquirePair(Ball a, Ball b) {
        var lockA = entries.get(a);
        var lockB = entries.get(b);

        lockA.lock();
        lockB.lock();
    }

    public void releasePair(Ball a, Ball b) {
        entries.get(a).unlock();
        if (a != b) {
            entries.get(b).unlock();
        }
    }
}
