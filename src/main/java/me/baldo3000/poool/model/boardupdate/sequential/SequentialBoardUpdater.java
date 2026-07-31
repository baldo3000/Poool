package me.baldo3000.poool.model.boardupdate.sequential;

import me.baldo3000.poool.model.Ball;
import me.baldo3000.poool.model.boardupdate.BoardUpdater;
import me.baldo3000.poool.model.utils.Boundary;
import me.baldo3000.poool.model.utils.GameBalls;

public class SequentialBoardUpdater implements BoardUpdater {
    private final GameBalls gameBalls;
    private final Boundary bounds;

    public SequentialBoardUpdater(GameBalls gameBalls, Boundary bounds) {
        this.gameBalls = gameBalls;
        this.bounds = bounds;
    }


    @Override
    public void updateBoard(long elapsedTime) {
        gameBalls.playerBall().updateState(elapsedTime, bounds);
        gameBalls.cpuBall().updateState(elapsedTime, bounds);

        var balls = gameBalls.balls();

        for (var b : balls) {
            b.updateState(elapsedTime, bounds);
        }

        for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball.resolveCollision(balls.get(i), balls.get(j));
            }
        }

        for (var b : balls) {
            Ball.resolveCollision(gameBalls.playerBall(), b);
            Ball.resolveCollision(gameBalls.cpuBall(), b);
        }

        Ball.resolveCollision(gameBalls.playerBall(), gameBalls.cpuBall());
    }
}
