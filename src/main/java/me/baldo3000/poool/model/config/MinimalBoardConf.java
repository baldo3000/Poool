package me.baldo3000.poool.model.config;

import me.baldo3000.poool.model.Ball;
import me.baldo3000.poool.model.BallType;
import me.baldo3000.poool.model.utils.Boundary;
import me.baldo3000.poool.model.utils.P2d;
import me.baldo3000.poool.model.utils.V2d;

import java.util.ArrayList;
import java.util.List;

public class MinimalBoardConf implements BoardConf {

    @Override
    public Ball getPlayerBall() {
        return new Ball(new P2d(0, 0), 0.06, 1, new V2d(0, 0.5), BallType.PLAYER, 0);
    }

    @Override
    public Ball getCpuBall() {
        return new Ball(new P2d(0.75, 0.75), 0.05, 1.5, new V2d(0, 1), BallType.CPU, 1);
    }

    @Override
    public List<Ball> getHoles() {
        return List.of();
    }

    @Override
    public List<Ball> getSmallBalls() {
        var id = 2;
        var balls = new ArrayList<Ball>();
        var b1 = new Ball(new P2d(0, 0.5), 0.05, 0.75, new V2d(0, 0), BallType.BALL, id++);
        var b2 = new Ball(new P2d(0.05, 0.55), 0.025, 0.25, new V2d(0, 0), BallType.BALL, id++);
        balls.add(b1);
        balls.add(b2);
        return balls;
    }

    @Override
    public Boundary getBoardBoundary() {
        return new Boundary(-1.5, -1.0, 1.5, 1.0);
    }

}
