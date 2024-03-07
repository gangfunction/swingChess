package hello;

import javax.swing.*;
import java.awt.*;

public class App {
    private JPanel panel;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            JFrame frame = new JFrame("App");
            frame.setContentPane(new JPanel());
            frame.setSize(600, 600);
            frame.setLayout(new GridLayout(8, 8));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });

    }
}