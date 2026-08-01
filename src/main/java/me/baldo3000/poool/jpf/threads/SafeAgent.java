package me.baldo3000.poool.jpf.threads;

import me.baldo3000.poool.model.Ball;
import me.baldo3000.poool.model.boardupdate.BallAllocator;
import me.baldo3000.poool.model.boardupdate.threads.Task;
import me.baldo3000.poool.model.utils.CyclicBarrier;
import me.baldo3000.poool.model.utils.GameBalls;
import me.baldo3000.poool.model.utils.UnboundedBuffer;

public class SafeAgent extends Thread {
    private final UnboundedBuffer<Task> taskBuffer = new UnboundedBuffer<>();
    private final GameBalls gameBalls;
    private final BallAllocator ballAllocator;
    private final CyclicBarrier collisionBarrier;
    private final CyclicBarrier updateBarrier;

    public SafeAgent(GameBalls gameBalls, BallAllocator ballAllocator, CyclicBarrier updateBarrier, CyclicBarrier collisionBarrier) {
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
        try {
            while (!Thread.currentThread().isInterrupted()) {

                var task = taskBuffer.get();

                for (var ball : task.myBalls()) {
                    ball.updateState(task.elapsedTime(), task.bounds());
                }

                updateBarrier.await();

                for (var ball1 : task.myBalls()) {
                    for (var ball2 : balls) {
                        tryCollision(ball1, ball2);
                    }
                    tryCollision(playerBall, ball1);
                    tryCollision(cpuBall, ball1);
                }
                collisionBarrier.await();
            }
        } catch (InterruptedException ignored) {
        }
    }

    // The only change from the original version is skipping the contact checking before acquiring lock on pair
    private void tryCollision(Ball a, Ball b) throws InterruptedException{
        if (a.getId() < b.getId()) {
            ballAllocator.acquirePair(a, b);
            try {
                Ball.resolveCollision(a, b);
            } finally {
                ballAllocator.releasePair(a, b);
            }
        }
    }

    public void notifyTask(Task task) {
        try {
            taskBuffer.put(task);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}

