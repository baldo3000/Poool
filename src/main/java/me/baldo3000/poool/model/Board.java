package me.baldo3000.poool.model;

import me.baldo3000.poool.model.boardupdate.BoardUpdater;
import me.baldo3000.poool.model.config.BoardConf;
import me.baldo3000.poool.model.utils.Boundary;
import me.baldo3000.poool.model.utils.GameBalls;
import me.baldo3000.poool.model.utils.V2d;

import java.util.Iterator;

public class Board {

    private final GameBalls gameBalls;
    private final Boundary bounds;
    private int playerScore = 0;
    private int cpuScore = 0;
    private boolean playerAlive = true;
    private boolean cpuAlive = true;

    public Board(BoardConf conf) {
        bounds = conf.getBoardBoundary();
        var balls = conf.getSmallBalls();
        var playerBall = conf.getPlayerBall();
        var cpuBall = conf.getCpuBall();
        var holes = conf.getHoles();
        gameBalls = new GameBalls(playerBall, cpuBall, balls, holes);
    }

    public void updateState(long dt, BoardUpdater boardUpdater) {
        boardUpdater.updateBoard(dt);

        Iterator<Ball> iterator = gameBalls.balls().iterator();
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            for (var hole : gameBalls.holes()) {
                if (isInHole(ball, hole)) {
                    switch (ball.getHitter()) {
                        case PLAYER -> playerScore++;
                        case CPU -> cpuScore++;
                        default -> {
                        }
                    }
                    iterator.remove();
                    break;
                }
            }
        }

        for (Ball hole : gameBalls.holes()) {
            if (isInHole(gameBalls.playerBall(), hole)) {
                playerAlive = false;
                return;
            }
            if (isInHole(gameBalls.cpuBall(), hole)) {
                cpuAlive = false;
                return;
            }
        }
    }


    public GameBalls getGameBalls() {
        return gameBalls;
    }

    public Boundary getBounds() {
        return bounds;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public int getCpuScore() {
        return cpuScore;
    }

    public boolean isPlayerAlive() {
        return playerAlive;
    }

    public boolean isCpuAlive() {
        return cpuAlive;
    }

    // TODO: check this condition
    private boolean isInHole(Ball b, Ball hole) {
        double dx = b.getPos().x() - hole.getPos().x();
        double dy = b.getPos().y() - hole.getPos().y();
        double distanceSquared = dx * dx + dy * dy;

        // A ball is in the hole if its center enters the hole's radius
        double holeRadius = hole.getRadius();
        return distanceSquared < (holeRadius * holeRadius);
    }

    public void applyInputToPlayer(V2d impulse) {
        this.gameBalls.playerBall().kick(impulse, 1.0);
    }

    public void applyInputToCpu(V2d impulse) {
        this.gameBalls.cpuBall().kick(impulse, Double.POSITIVE_INFINITY);
    }
}
