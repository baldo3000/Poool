package me.baldo3000.poool.controller;

import me.baldo3000.poool.model.Board;
import me.baldo3000.poool.model.boardupdate.BoardUpdater;
import me.baldo3000.poool.model.utils.V2d;
import me.baldo3000.poool.view.View;
import me.baldo3000.poool.view.ViewModel;

public class Controller implements GameEventListener {
    private final GameEventBuffer eventBuffer = new GameEventBuffer();
    private final Board board;
    private final BoardUpdater boardUpdater;
    private final View view;
    private final ViewModel viewModel;

    public Controller(Board board, BoardUpdater boardUpdater) {
        this.board = board;
        this.viewModel = new ViewModel();
        this.boardUpdater = boardUpdater;
        this.view = new View(viewModel, this, 1200, 800);
    }

    public void start() {
        long lastFrameTimestamp = System.currentTimeMillis();

        while (true) {
            long now = System.currentTimeMillis();
            long dt = now - lastFrameTimestamp;
            lastFrameTimestamp = now;

            eventBuffer.getLastPlayerDirection().ifPresent(board::applyInputToPlayer);
            eventBuffer.getLastCpuDirection().ifPresent(board::applyInputToCpu);

            board.updateState(dt, boardUpdater);

            viewModel.update(board, dt);
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
