package hello;

import javax.swing.*;
import java.awt.*;


public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{

            JFrame primaryFrame = new JFrame("App");
            primaryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            primaryFrame.setSize(600, 600);

            ChessBoard chessBoard = new ChessBoard();
            primaryFrame.setContentPane(chessBoard.getBoardPanel());


            primaryFrame.setVisible(true);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            primaryFrame.setLocation(dim.width/2-primaryFrame.getSize().width/2, dim.height/2-primaryFrame.getSize().height/2);
            ChessGameLauncher.createAndShowGUI(primaryFrame);

        });



    }
}