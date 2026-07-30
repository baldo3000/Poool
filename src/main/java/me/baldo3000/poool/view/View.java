package me.baldo3000.poool.view;

import me.baldo3000.poool.controller.GameEventListener;

import javax.swing.*;

public class View {

    private final ViewFrame frame;
    private final ViewModel viewModel;

    public View(ViewModel model, GameEventListener listener, int w, int h) {
        frame = new ViewFrame(model, listener, w, h);
        viewModel = model;
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
        });
    }

    public void render() {
        frame.render();
    }
}
