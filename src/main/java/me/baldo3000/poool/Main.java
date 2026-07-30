package me.baldo3000.poool;

import me.baldo3000.poool.controller.Controller;
import me.baldo3000.poool.controller.CpuAgent;
import me.baldo3000.poool.model.Board;
import me.baldo3000.poool.model.config.LargeBoardConf;

public class Main {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        var board = new Board(new LargeBoardConf());
        var controller = new Controller(board);
        var cpuAgent = new CpuAgent(controller);
        cpuAgent.start();
        controller.start();
    }
}
