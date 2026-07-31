package me.baldo3000.poool.view;

import me.baldo3000.poool.model.Board;

import java.util.ArrayList;
import java.util.List;

public class ViewModel {
    private static final int BUFFER_SIZE = 32;

    private final long[] frameTimes = new long[BUFFER_SIZE];
    private int frameCount = 0;

    private final List<BallViewInfo> balls = new ArrayList<>();
    private BallViewInfo player = null;
    private BallViewInfo cpu = null;
    private final List<BallViewInfo> holes = new ArrayList<>();
    private int playerScore = 0;
    private int cpuScore = 0;

    public synchronized void update(Board board, long elapsedTime) {
        frameTimes[frameCount % BUFFER_SIZE] = elapsedTime;
        frameCount++;

        this.playerScore = board.getPlayerScore();
        this.cpuScore = board.getCpuScore();

        var gameBalls = board.getGameBalls();

        balls.clear();
        for (var b : gameBalls.balls()) {
            balls.add(new BallViewInfo(b.getPos(), b.getRadius()));
        }

        var playerBall = gameBalls.playerBall();
        var cpuBall = board.getGameBalls().cpuBall();
        player = board.isPlayerAlive() ? new BallViewInfo(playerBall.getPos(), playerBall.getRadius()) : null;
        cpu = board.isCpuAlive() ? new BallViewInfo(cpuBall.getPos(), cpuBall.getRadius()) : null;

        holes.clear();
        for (var h : gameBalls.holes()) {
            holes.add(new BallViewInfo(h.getPos(), h.getRadius()));
        }
    }

    public synchronized List<BallViewInfo> getBalls() {
        return new ArrayList<>(balls);
    }

    public synchronized double getFramePerSec() {
        long sum = 0;
        for (long ft : frameTimes) {
            if (ft == 0) continue;
            sum += ft;
        }
        return 1000.0 / ((double) sum / BUFFER_SIZE);
    }

    public synchronized BallViewInfo getPlayerBall() {
        return player;
    }

    public synchronized BallViewInfo getCpuBall() {
        return cpu;
    }

    public synchronized List<BallViewInfo> getHoles() {
        return new ArrayList<>(holes);
    }

    public synchronized int getPlayerScore() {
        return playerScore;
    }

    public synchronized int getCpuScore() {
        return cpuScore;
    }
}