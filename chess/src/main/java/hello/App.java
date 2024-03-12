package hello;

import hello.command.CommandInvoker;
import hello.core.ChessGameTurn;
import hello.gameobject.ChessBoardUI;
import hello.gameobject.ChessGameLogic;
import hello.gameobject.ChessGameState;
import hello.observer.ChessObserver;

import javax.swing.*;
import java.awt.*;


public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            JFrame primaryFrame = new JFrame("App");
            primaryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            primaryFrame.setSize(600, 600);

            ChessBoardUI chessBoardUI = getChessBoardUI();


            primaryFrame.setContentPane(chessBoardUI.getBoardPanel());
            primaryFrame.setVisible(true);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            primaryFrame.setLocation(dim.width / 2 - primaryFrame.getSize().width / 2, dim.height / 2 - primaryFrame.getSize().height / 2);
            ChessGameLauncher.createAndShowGUI(primaryFrame);


        });


    }

    private static ChessBoardUI getChessBoardUI() {
        ChessGameState chessGameState = new ChessGameState();
        CommandInvoker commandInvoker = new CommandInvoker();
        ChessGameTurn chessGameTurn = new ChessGameTurn();
        ChessObserver observer = new ChessObserver();
        GameLog gameLog = new GameLog(observer);
        ChessGameLogic chessGameLogic = new ChessGameLogic(chessGameTurn, commandInvoker, chessGameState, gameLog);
        ChessBoardUI chessBoardUI = new ChessBoardUI(chessGameState, chessGameLogic);
        chessGameLogic.setChessBoardUI(chessBoardUI);
        chessGameLogic.setGameEventListener(chessBoardUI);
        return chessBoardUI;
    }


}