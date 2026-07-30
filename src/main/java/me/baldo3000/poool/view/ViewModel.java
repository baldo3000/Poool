package me.baldo3000.poool.view;

import me.baldo3000.poool.model.Board;

import java.util.ArrayList;
import java.util.List;

public class ViewModel {

    private final List<BallViewInfo> balls = new ArrayList<>();
    private BallViewInfo player = null;
    private BallViewInfo cpu = null;
    private final List<BallViewInfo> holes = new ArrayList<>();
    private int framePerSec = 0;
    private int playerScore = 0;
    private int cpuScore = 0;

    public synchronized void update(Board board, int framePerSec) {

        this.playerScore = board.getPlayerScore();
        this.cpuScore = board.getCpuScore();

        balls.clear();
        for (var b : board.getBalls()) {
            balls.add(new BallViewInfo(b.getPos(), b.getRadius()));
        }
        this.framePerSec = framePerSec;
        var playerBall = board.getPlayerBall();
        var cpuBall = board.getCpuBall();
        player = board.isPlayerAlive() ? new BallViewInfo(playerBall.getPos(), playerBall.getRadius()) : null;
        cpu = board.isCpuAlive() ? new BallViewInfo(cpuBall.getPos(), cpuBall.getRadius()) : null;

        holes.clear();
        for (var h : board.getHoles()) {
            holes.add(new BallViewInfo(h.getPos(), h.getRadius()));
        }
    }

    public synchronized List<BallViewInfo> getBalls() {
        return new ArrayList<>(balls);
    }

    public synchronized int getFramePerSec() {
        return framePerSec;
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