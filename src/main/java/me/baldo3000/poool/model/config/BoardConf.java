package me.baldo3000.poool.model.config;

import me.baldo3000.poool.model.Ball;
import me.baldo3000.poool.model.utils.Boundary;

import java.util.List;

public interface BoardConf {

    Boundary getBoardBoundary();

    Ball getPlayerBall();

    List<Ball> getSmallBalls();

    Ball getCpuBall();

    List<Ball> getHoles();
}