package me.baldo3000.poool.model;

import me.baldo3000.poool.model.config.BoardConf;
import me.baldo3000.poool.model.utils.Boundary;
import me.baldo3000.poool.model.utils.V2d;

import java.util.Iterator;
import java.util.List;

public class Board {

    private final List<Ball> balls;
    private final Ball playerBall;
    private final Boundary bounds;
    private final Ball cpuBall;
    private final List<Ball> holes;
    private int playerScore = 0;
    private int cpuScore = 0;
    private boolean playerAlive = true;
    private boolean cpuAlive = true;

    public Board(BoardConf conf) {
        balls = conf.getSmallBalls();
        playerBall = conf.getPlayerBall();
        cpuBall = conf.getCpuBall();
        bounds = conf.getBoardBoundary();
        holes = conf.getHoles();
    }

    public void updateState(long dt) {
        playerBall.updateState(dt, this);
        cpuBall.updateState(dt, this);

        for (var b : balls) {
            b.updateState(dt, this);
        }


        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            for (var hole : holes) {
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

        // TODO: check this condition
        // 3. Player/CPU Hole
        for (Ball hole : holes) {
            if (isInHole(playerBall, hole)) {
                playerAlive = false;
                return;
            }
            if (isInHole(cpuBall, hole)) {
                cpuAlive = false;
                return;
            }
        }


        for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball.resolveCollision(balls.get(i), balls.get(j));
            }
        }

        for (var b : balls) {
            Ball.resolveCollision(playerBall, b);
            Ball.resolveCollision(cpuBall, b);
        }

        Ball.resolveCollision(playerBall, cpuBall);
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public Ball getPlayerBall() {
        return playerBall;
    }

    public Ball getCpuBall() {
        return cpuBall;
    }

    public List<Ball> getHoles() {
        return holes;
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
        this.playerBall.kick(impulse, 1.0);
    }

    public void applyInputToCpu(V2d impulse) {
        this.cpuBall.kick(impulse, Double.POSITIVE_INFINITY);
    }
}
