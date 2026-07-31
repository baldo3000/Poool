package me.baldo3000.poool.model.boardupdate.threads;

import me.baldo3000.poool.model.Ball;
import me.baldo3000.poool.model.boardupdate.BallAllocator;
import me.baldo3000.poool.model.boardupdate.BoardUpdater;
import me.baldo3000.poool.model.utils.Boundary;
import me.baldo3000.poool.model.utils.CyclicBarrier;
import me.baldo3000.poool.model.utils.GameBalls;

import java.util.ArrayList;
import java.util.List;

public class ThreadsBallUpdater implements BoardUpdater {

    private final int nThreads;
    private final List<CollisionAgent> collisionAgents;
    private final CyclicBarrier updateBarrier;

    public ThreadsBallUpdater(List<Ball> initialBalls, int nThreads) {
        var ballAllocator = new BallAllocator(initialBalls);
        this.nThreads = nThreads;
        collisionAgents = new ArrayList<>(nThreads);
        updateBarrier = new CyclicBarrier(nThreads + 1);
        for (int i = 0; i < nThreads; i++) {
            var agent = new CollisionAgent(ballAllocator, updateBarrier);
            collisionAgents.add(agent);
            agent.start();
        }
    }

    @Override
    public void updateStates(GameBalls gameBalls, Boundary bounds, long elapsedTime) {
        gameBalls.playerBall().updateState(elapsedTime, bounds);
        gameBalls.cpuBall().updateState(elapsedTime, bounds);

        for (var b : gameBalls.balls()) {
            b.updateState(elapsedTime, bounds);
        }
    }

    @Override
    public void resolveCollisions(GameBalls gameBalls) {
        var balls = gameBalls.balls();
        var splitBalls = gameBalls.splitSimpleBalls(nThreads);

        for (int i = 0; i < nThreads; i++) {
            collisionAgents.get(i).notifyTask(new CollisionTask(splitBalls.get(i), balls));
        }

        updateBarrier.await();

        for (var b : balls) {
            Ball.resolveCollision(gameBalls.playerBall(), b);
            Ball.resolveCollision(gameBalls.cpuBall(), b);
        }

        Ball.resolveCollision(gameBalls.playerBall(), gameBalls.cpuBall());
    }
}
