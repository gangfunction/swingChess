package hello;

import hello.core.ChessGame;
import hello.gameobject.ChessBoard;
import hello.observer.ChessObserver;

import javax.swing.*;
import java.awt.*;


public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            JFrame primaryFrame = new JFrame("App");
            primaryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            primaryFrame.setSize(600, 600);

            String playerName1 = JOptionPane.showInputDialog(primaryFrame, "Enter name for Player 1 (White):");
            String playerName2 = JOptionPane.showInputDialog(primaryFrame, "Enter name for Player 2 (Black):");
            ChessGame chessGame = new ChessGame();
            ChessObserver chessObserver = new ChessObserver(chessGame);
            GameLog gameLog = new GameLog(chessObserver);
            ChessBoard chessBoard = new ChessBoard(gameLog);


            // ChessBoard를 CENTER 위치에 배치
            primaryFrame.add(chessBoard.getBoardPanel(), BorderLayout.CENTER);



            primaryFrame.setContentPane(chessBoard.getBoardPanel());
            primaryFrame.setVisible(true);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            primaryFrame.setLocation(dim.width / 2 - primaryFrame.getSize().width / 2, dim.height / 2 - primaryFrame.getSize().height / 2);
            ChessGameLauncher.createAndShowGUI(primaryFrame);


        });


    }
}