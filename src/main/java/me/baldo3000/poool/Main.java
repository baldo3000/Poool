package me.baldo3000.poool;

import me.baldo3000.poool.controller.Controller;
import me.baldo3000.poool.controller.CpuAgent;
import me.baldo3000.poool.model.Board;
import me.baldo3000.poool.model.boardupdate.threads.ThreadsBallUpdater;
import me.baldo3000.poool.model.config.MassiveBoardConf;

public class Main {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        var nThreads = Runtime.getRuntime().availableProcessors() + 1;
        System.out.println("Using " + nThreads + " threads");
        //var board = new Board(new LargeBoardConf());
        var board = new Board(new MassiveBoardConf());
        //var controller = new Controller(board, new SequentialBoardUpdater());
        var controller = new Controller(board, new ThreadsBallUpdater(board.getGameBalls().getAllBalls(), nThreads));
        var cpuAgent = new CpuAgent(controller);
        cpuAgent.start();
        controller.start();
    }
}
