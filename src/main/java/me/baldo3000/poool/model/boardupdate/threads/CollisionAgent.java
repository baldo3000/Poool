package me.baldo3000.poool.model.boardupdate.threads;

import me.baldo3000.poool.model.Ball;
import me.baldo3000.poool.model.boardupdate.BallAllocator;
import me.baldo3000.poool.model.utils.CyclicBarrier;
import me.baldo3000.poool.model.utils.UnboundedBuffer;

public class CollisionAgent extends Thread {
    private final UnboundedBuffer<CollisionTask> collisionTaskBuffer = new UnboundedBuffer<>();
    private final BallAllocator ballAllocator;
    private final CyclicBarrier barrier;
    private volatile boolean running = true;

    public CollisionAgent(BallAllocator ballAllocator, CyclicBarrier barrier) {
        this.ballAllocator = ballAllocator;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        while (running) {
            try {
                var task = collisionTaskBuffer.get();
                for (var ball1 : task.myBalls()) {
                    for (var ball2 : task.others()) {
                        if (ball1.getId() < ball2.getId() && Ball.isInContact(ball1, ball2)) {
                            ballAllocator.acquirePair(ball1, ball2);
                            try {
                                Ball.resolveCollision(ball1, ball2);
                            } finally {
                                ballAllocator.releasePair(ball1, ball2);
                            }
                        }
                    }

                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            barrier.await();
        }
    }

    public void notifyTask(CollisionTask task) {
        collisionTaskBuffer.put(task);
    }

    public void stopAgent() {
        this.running = false;
    }
}
