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
            JFrame primaryFrame = createPrimaryFrame();

            ChessBoardUI chessBoardUI = initializeChessComponents(primaryFrame);

            ChessGameLauncher.createAndShowGUI(primaryFrame);

            primaryFrame.setContentPane(chessBoardUI.getBoardPanel());
            centerFrameOnScreen(primaryFrame);
            primaryFrame.setVisible(true);
        });
    }

    private static JFrame createPrimaryFrame() {
        JFrame primaryFrame = new JFrame("Chess Game");
        primaryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        primaryFrame.setSize(600, 600);
        return primaryFrame;
    }

    private static ChessBoardUI initializeChessComponents(JFrame primaryFrame) {
        ChessGameState chessGameState = new ChessGameState();
        CommandInvoker commandInvoker = new CommandInvoker();
        ChessGameTurn chessGameTurn = new ChessGameTurn();
        ChessObserver observer = new ChessObserver();
        GameLog gameLog = new GameLog(observer);
        ChessGameLogic chessGameLogic = new ChessGameLogic(chessGameTurn, commandInvoker, gameLog);
        ChessBoardUI chessBoardUI = new ChessBoardUI(chessGameState);

        chessGameLogic.setGameEventListener(chessBoardUI, chessGameState);
        chessBoardUI.setGameLogicActions(chessGameLogic);

        return chessBoardUI;
    }

    private static void centerFrameOnScreen(JFrame frame) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
    }
}