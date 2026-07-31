package me.baldo3000.poool.model.utils;

import me.baldo3000.poool.model.Ball;

import java.util.ArrayList;
import java.util.List;

public record GameBalls(Ball playerBall, Ball cpuBall, List<Ball> balls, List<Ball> holes) {

    public List<Ball> getAllBalls() {
        var allBalls = new ArrayList<Ball>(balls.size() + holes.size() + 2);
        allBalls.addAll(balls);
        allBalls.add(playerBall);
        allBalls.add(cpuBall);
        allBalls.addAll(holes);
        return allBalls;
    }

    public List<List<Ball>> splitSimpleBalls(int n) {
        List<Ball> allBalls = new ArrayList<>(balls);
        List<List<Ball>> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(new ArrayList<>());
        }
        for (int i = 0; i < allBalls.size(); i++) {
            result.get(i % n).add(allBalls.get(i));
        }
        return result;
    }
}
