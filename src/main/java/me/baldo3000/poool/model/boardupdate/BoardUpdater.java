package me.baldo3000.poool.model.boardupdate;

import me.baldo3000.poool.model.utils.Boundary;
import me.baldo3000.poool.model.utils.GameBalls;

public interface BoardUpdater {

    void updateStates(GameBalls gameBalls, Boundary bounds, long elapsedTime);

    void resolveCollisions(GameBalls gameBalls);
}
