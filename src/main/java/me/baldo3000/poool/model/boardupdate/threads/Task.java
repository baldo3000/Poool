package me.baldo3000.poool.model.boardupdate.threads;

import me.baldo3000.poool.model.Ball;
import me.baldo3000.poool.model.utils.Boundary;

import java.util.List;

public record Task(List<Ball> myBalls, Boundary bounds, long elapsedTime) {
}
