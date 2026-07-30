package me.baldo3000.poool.controller;

import me.baldo3000.poool.model.Board;
import me.baldo3000.poool.model.utils.V2d;
import me.baldo3000.poool.view.View;
import me.baldo3000.poool.view.ViewModel;

public class Controller implements GameEventListener {
    private final GameEventBuffer eventBuffer = new GameEventBuffer();
    private final Board board;
    private final View view;
    private final ViewModel viewModel;

    public Controller(Board board) {
        this.board = board;
        this.viewModel = new ViewModel();
        this.view = new View(viewModel, this, 1200, 800);
    }

    public void start() {
        int nFrames = 0;
        long t0 = System.currentTimeMillis();
        long lastUpdateTime = System.currentTimeMillis();

        while (true) {
            long elapsed = System.currentTimeMillis() - lastUpdateTime;
            lastUpdateTime = System.currentTimeMillis();

            eventBuffer.getLastPlayerDirection().ifPresent(board::applyInputToPlayer);
            eventBuffer.getLastCpuDirection().ifPresent(board::applyInputToCpu);

            board.updateState(elapsed);

            nFrames++;
            int framePerSec = 0;
            long dt = (System.currentTimeMillis() - t0);
            if (dt > 0) {
                framePerSec = (int) (nFrames * 1000 / dt);
            }

            viewModel.update(board, framePerSec);
            view.render();
        }
    }

    @Override
    public void notifyPlayerInput(V2d vel) {
        eventBuffer.onPlayerInput(vel);
    }

    @Override
    public void notifyCpuInput(V2d vel) {
        eventBuffer.onCpuInput(vel);
    }
}
