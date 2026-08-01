package me.baldo3000.poool.model.boardupdate.executor;

import me.baldo3000.poool.model.Ball;
import me.baldo3000.poool.model.boardupdate.BallAllocator;
import me.baldo3000.poool.model.boardupdate.BoardUpdater;
import me.baldo3000.poool.model.utils.Boundary;
import me.baldo3000.poool.model.utils.GameBalls;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorBoardUpdater implements BoardUpdater {

    private final GameBalls gameBalls;
    private final Boundary bounds;
    private final int nThreads;
    private final ExecutorService executor;
    private final BallAllocator ballAllocator;

    public ExecutorBoardUpdater(GameBalls gameBalls, Boundary bounds, int nThreads) {
        this.gameBalls = gameBalls;
        this.bounds = bounds;
        this.ballAllocator = new BallAllocator(gameBalls.getAllBalls());
        this.nThreads = nThreads;
        this.executor = Executors.newFixedThreadPool(nThreads);
    }

    @Override
    public void updateBoard(long elapsedTime) {
        gameBalls.playerBall().updateState(elapsedTime, bounds);
        gameBalls.cpuBall().updateState(elapsedTime, bounds);

        var splitBalls = gameBalls.splitSimpleBalls(nThreads);

        List<Future<?>> updateResults = new ArrayList<>(nThreads);
        for (var balls : splitBalls) {
            updateResults.add(executor.submit(new UpdateTask(balls, elapsedTime)));
        }
        waitAllFutures(updateResults);

        List<Future<?>> collisionResults = new ArrayList<>(nThreads);
        for (var balls : splitBalls) {
            collisionResults.add(executor.submit(new CollisionTask(balls)));
        }
        waitAllFutures(collisionResults);

        Ball.resolveCollision(gameBalls.playerBall(), gameBalls.cpuBall());
    }

    private void waitAllFutures(List<Future<?>> futures) {
        for (var f : futures) {
            try {
                f.get();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class UpdateTask implements Runnable {
        private final List<Ball> myBalls;
        private final long dt;

        public UpdateTask(List<Ball> myBalls, long elapsedTime) {
            this.myBalls = myBalls;
            this.dt = elapsedTime;
        }

        @Override
        public void run() {
            for (var ball : myBalls) {
                ball.updateState(dt, bounds);
            }
        }
    }

    private class CollisionTask implements Runnable {
        private final List<Ball> myBalls;

        public CollisionTask(List<Ball> myBalls) {
            this.myBalls = myBalls;
        }

        @Override
        public void run() {
            for (var ball1 : myBalls) {
                for (var ball2 : gameBalls.balls()) {
                    tryCollision(ball1, ball2);
                }
                tryCollision(gameBalls.playerBall(), ball1);
                tryCollision(gameBalls.cpuBall(), ball1);
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
    }
}
