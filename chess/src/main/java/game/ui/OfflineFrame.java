package game.ui;

import game.core.ChessGameTurn;
import game.util.Color;
import game.core.Player;
import game.core.PlayerManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OfflineFrame extends JFrame {
    ChessGameTurn chessGameTurn;
    JTextField userTextField;
    JTextField opponentTextField;
    JButton startButton;
    public OfflineFrame() {
        setLayout( new FlowLayout(FlowLayout.CENTER));
        setLocationRelativeTo(null);
        add(new JLabel("Offline Mode"));
        setSize(300, 200);
        userTextField = new JTextField(20);
        add(new JLabel("Username:"));
        add(userTextField);

        opponentTextField = new JTextField(20);
        add(new JLabel("Opponent:"));
        add(opponentTextField);

        startButton = new JButton("Start");
        add(startButton);

        setVisible(true);
        startButton.addActionListener(e -> {
            String user = userTextField.getText();
            String opponent = opponentTextField.getText();
            if (user.isEmpty() || opponent.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both usernames.");
            } else {
                JOptionPane.showMessageDialog(null, "Starting game...");
            }
            this.dispose();

            PlayerManager.setPlayers(List.of(new Player(user, Color.WHITE), new Player(opponent,Color.BLACK)));


        });

    }
}
