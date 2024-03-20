package game;

import game.command.CommandInvoker;
import game.core.ChessGameTurn;
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

            ChessGameState chessGameState = new ChessGameState();
            CommandInvoker commandInvoker = new CommandInvoker();
            ChessGameTurn chessGameTurn = new ChessGameTurn();
            ChessGameLogic chessGameLogic = new ChessGameLogic(chessGameTurn, commandInvoker);
            ChessBoardUI chessBoardUI = new ChessBoardUI(chessGameState);

            chessGameLogic.setGameEventListener(chessBoardUI, chessGameState);
            chessBoardUI.setGameLogicActions(chessGameLogic);

            ChessGameLauncher.createAndShowGUI(primaryFrame);

            primaryFrame.setContentPane(chessBoardUI.getBoardPanel());
            centerFrameOnScreen(primaryFrame);
            primaryFrame.setVisible(true);
            JFrame logFrame = createLogFrame(primaryFrame, chessGameTurn);

            logFrame.setVisible(true);
        });
    }

    private static JFrame createLogFrame(JFrame primaryFrame, ChessGameTurn chessGameTurn) {
        JFrame logFrame = new JFrame("Game Log");
        logFrame.setSize(600, 100);
        logFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int x = primaryFrame.getLocation().x;
        int y = primaryFrame.getLocation().y + primaryFrame.getHeight();
        logFrame.setLocation(x, y);

        JTextArea textArea = new JTextArea();
        logFrame.setVisible(true);
        ChessObserver observer = new ChessObserver();
        GameLog gameLog = new GameLog(observer, textArea);
        chessGameTurn.addObserver(gameLog);

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        logFrame.add(scrollPane, BorderLayout.CENTER);
        return logFrame;
    }

    private static JFrame createPrimaryFrame() {
        JFrame primaryFrame = new JFrame("Chess Game");
        primaryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        primaryFrame.setSize(600, 600);
        return primaryFrame;
    }



    private static void centerFrameOnScreen(JFrame frame) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
    }
}