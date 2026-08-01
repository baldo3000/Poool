package me.baldo3000.poool.jpf;

import me.baldo3000.poool.jpf.threads.SafeThreadsBoardUpdater;
import me.baldo3000.poool.model.Ball;
import me.baldo3000.poool.model.BallType;
import me.baldo3000.poool.model.utils.Boundary;
import me.baldo3000.poool.model.utils.GameBalls;
import me.baldo3000.poool.model.utils.P2d;
import me.baldo3000.poool.model.utils.V2d;

import java.util.List;

public class JpfScenarioThreads {
    private static final int THREADS = 2;
    private static final double SIZE = 10.0;

    public static void main(String[] args) {
        var bounds = new Boundary(-SIZE, -SIZE, SIZE, SIZE);

        // PLayer and cpu collide
        var player = new Ball(new P2d(0.00, 0.00), 0.10, 1.5, new V2d(0.20, 0.00), BallType.PLAYER, 0);
        var cpu = new Ball(new P2d(0.05, 0.00), 0.10, 1.5, new V2d(-0.20, 0.00), BallType.CPU, 1);

        // Two balls collide
        var balls = List.of(
                new Ball(new P2d(0.10, 0.00), 0.10, 1.0, new V2d(0.00, 0.15), BallType.BALL, 2),
                new Ball(new P2d(0.15, 0.00), 0.10, 1.0, new V2d(0.00, -0.15), BallType.BALL, 3)
        );

        var gameBalls = new GameBalls(player, cpu, balls, List.of());

        var boardUpdater = new SafeThreadsBoardUpdater(gameBalls, bounds, THREADS);
        boardUpdater.updateBoard(16L);
        boardUpdater.stopAgents();
    }
}
