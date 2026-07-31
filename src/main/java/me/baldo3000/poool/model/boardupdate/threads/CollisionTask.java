package me.baldo3000.poool.model.boardupdate.threads;

import me.baldo3000.poool.model.Ball;

import java.util.List;

public record CollisionTask(List<Ball> myBalls, List<Ball> others) {
}
