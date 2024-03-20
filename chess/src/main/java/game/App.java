package game;

import game.command.CommandInvoker;
import game.core.ChessGameTurn;
import game.object.CastlingLogic;
import game.object.ChessBoardUI;
import game.object.ChessGameLogic;
import game.object.ChessGameState;
import game.observer.ChessObserver;

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
        ChessGameLogic chessGameLogic = new ChessGameLogic(chessGameTurn, commandInvoker, gameLog);
        ChessBoardUI chessBoardUI = new ChessBoardUI(chessGameState);
        chessGameLogic.setGameEventListener(chessBoardUI,chessGameState);
        chessBoardUI.setGameLogicActions(chessGameLogic);
        CastlingLogic castlingLogic = new CastlingLogic();
        castlingLogic.setCastlingLogic(chessGameLogic,chessGameState,chessBoardUI);
        return chessBoardUI;
    }


}