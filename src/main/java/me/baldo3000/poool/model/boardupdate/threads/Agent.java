package me.baldo3000.poool.model.boardupdate.threads;

import me.baldo3000.poool.model.Ball;
import me.baldo3000.poool.model.boardupdate.BallAllocator;
import me.baldo3000.poool.model.utils.CyclicBarrier;
import me.baldo3000.poool.model.utils.GameBalls;
import me.baldo3000.poool.model.utils.UnboundedBuffer;

public class Agent extends Thread {
    private final UnboundedBuffer<Task> taskBuffer = new UnboundedBuffer<>();
    private final GameBalls gameBalls;
    private final BallAllocator ballAllocator;
    private final CyclicBarrier collisionBarrier;
    private final CyclicBarrier updateBarrier;
    private volatile boolean running = true;

    public Agent(GameBalls gameBalls, BallAllocator ballAllocator, CyclicBarrier updateBarrier, CyclicBarrier collisionBarrier) {
        this.gameBalls = gameBalls;
        this.ballAllocator = ballAllocator;
        this.collisionBarrier = collisionBarrier;
        this.updateBarrier = updateBarrier;
    }

    @Override
    public void run() {
        var balls = gameBalls.balls();
        var playerBall = gameBalls.playerBall();
        var cpuBall = gameBalls.cpuBall();
        while (running) {
            try {
                var task = taskBuffer.get();

                for (var ball : task.myBalls()) {
                    ball.updateState(task.elapsedTime(), task.bounds());
                }

                updateBarrier.await();

                for (var ball1 : task.myBalls()) {
                    for (var ball2 : balls) {
                        tryCollision(ball1, ball2);
                    }
                    tryCollision(ball1, playerBall);
                    tryCollision(ball1, cpuBall);
                }
                collisionBarrier.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void tryCollision(Ball a, Ball b) {
        if (a.getId() < b.getId() && Ball.isInContact(a, b)) {
            ballAllocator.acquirePair(a, b);
            try {
                Ball.resolveCollision(a, b);
            } finally {
                ballAllocator.releasePair(a, b);
            }
        }
    }

    public void notifyTask(Task task) {
        taskBuffer.put(task);
    }

    public void stopAgent() {
        this.running = false;
    }
}
