package me.baldo3000.poool.model.config;

import me.baldo3000.poool.model.Ball;
import me.baldo3000.poool.model.BallType;
import me.baldo3000.poool.model.utils.Boundary;
import me.baldo3000.poool.model.utils.P2d;
import me.baldo3000.poool.model.utils.V2d;

import java.util.ArrayList;
import java.util.List;

public class MassiveBoardConf implements BoardConf {
    private int id = 0;

    @Override
    public Ball getPlayerBall() {
        return new Ball(new P2d(-0.75, -0.75), 0.05, 1.5, new V2d(0, 0), BallType.PLAYER, id++);
    }

    @Override
    public Ball getCpuBall() {
        return new Ball(new P2d(0.75, -0.75), 0.05, 1.5, new V2d(0, 0), BallType.CPU, id++);
    }

    @Override
    public List<Ball> getHoles() {
        Ball h1 = new Ball(new P2d(-1.5, 1), 0.3, 1.5, new V2d(0, 0), BallType.HOLE, id++);
        Ball h2 = new Ball(new P2d(1.5, 1), 0.3, 1.5, new V2d(0, 0), BallType.HOLE, id++);
        return List.of(h1, h2);
    }

    @Override
    public List<Ball> getSmallBalls() {
        var ballRadius = 0.01;
        var balls = new ArrayList<Ball>();
        double spacing = 0.015;

        for (int row = 0; row < 45; row++) {
            for (int col = 0; col < 100; col++) {
                var px = -1.25 + col * spacing;
                var py = -0.5 + row * spacing;
                var b = new Ball(new P2d(px, py), ballRadius, 0.25, new V2d(0, 0), BallType.BALL, id++);
                balls.add(b);
            }
        }
        return balls;
    }

    public Boundary getBoardBoundary() {
        return new Boundary(-1.5, -1.0, 1.5, 1.0);
    }
}
