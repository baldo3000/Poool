package me.baldo3000.poool.model.boardupdate.threads;

import me.baldo3000.poool.model.Ball;
import me.baldo3000.poool.model.boardupdate.BallAllocator;
import me.baldo3000.poool.model.boardupdate.BoardUpdater;
import me.baldo3000.poool.model.utils.Boundary;
import me.baldo3000.poool.model.utils.CyclicBarrier;
import me.baldo3000.poool.model.utils.GameBalls;

import java.util.ArrayList;
import java.util.List;

public class ThreadsBoardUpdater implements BoardUpdater {

    private final GameBalls gameBalls;
    private final Boundary bounds;
    private final int nThreads;
    private final List<Agent> agents;
    private final CyclicBarrier collisionBarrier;

    public ThreadsBoardUpdater(GameBalls gameBalls, Boundary bounds, int nThreads) {
        this.gameBalls = gameBalls;
        this.bounds = bounds;
        var ballAllocator = new BallAllocator(gameBalls.getAllBalls());
        this.nThreads = nThreads;
        agents = new ArrayList<>(nThreads);
        var updateBarrier = new CyclicBarrier(nThreads);
        collisionBarrier = new CyclicBarrier(nThreads + 1);
        for (int i = 0; i < nThreads; i++) {
            var agent = new Agent(gameBalls, ballAllocator, updateBarrier, collisionBarrier);
            agents.add(agent);
            agent.start();
        }
    }

    @Override
    public void updateBoard(long elapsedTime) {
        gameBalls.playerBall().updateState(elapsedTime, bounds);
        gameBalls.cpuBall().updateState(elapsedTime, bounds);

        var splitBalls = gameBalls.splitSimpleBalls(nThreads);

        for (int i = 0; i < nThreads; i++) {
            agents.get(i).notifyTask(new Task(splitBalls.get(i), bounds, elapsedTime));
        }

        collisionBarrier.await();

        Ball.resolveCollision(gameBalls.playerBall(), gameBalls.cpuBall());
    }
}
