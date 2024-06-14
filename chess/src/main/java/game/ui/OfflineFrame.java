package game.ui;

import game.computer.ComputerPlayer;
import game.util.Color;
import game.core.PlayerManager;

import javax.swing.*;
import java.awt.*;

public class OfflineFrame extends JFrame {
    JTextField userTextField;
    JTextField opponentTextField;
    JLabel userNameLabel;
    JLabel opponentLabel;
    JPanel userPanel;
    JPanel opponentPanel;
    JButton startButton;
    JButton dualPlayerButton;
    JButton computerButton;
    public OfflineFrame(PlayerManager playerManager, ComputerPlayer computerPlayer) {
        setTitle("Offline Game");
        setLayout( new FlowLayout(FlowLayout.CENTER));
        setSize(300, 200);
        setLocationRelativeTo(null);
        userNameLabel = new JLabel();
        userNameLabel.setText("Player 1");

        userTextField = new JTextField(20);
        add(userTextField);

        userPanel = new JPanel();
        userPanel.add(userNameLabel);
        userPanel.add(userTextField);

        opponentLabel = new JLabel();
        opponentLabel.setText("Player 2");

        opponentTextField = new JTextField(20);
        add(opponentTextField);

        opponentPanel = new JPanel();
        opponentPanel.add(opponentLabel);
        opponentPanel.add(opponentTextField);

        add(userPanel);
        add(opponentPanel);

        userPanel.setVisible(false);
        opponentPanel.setVisible(false);


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
            playerManager.addPlayer(user, Color.WHITE);
            playerManager.addPlayer(opponent, Color.BLACK);
            playerManager.setCurrentPlayerIndex(0);
        });
        computerButton = new JButton("Play against computer");
        add(computerButton);
        computerButton.addActionListener(e -> {
            computerPlayer.setActive(true);
            playerManager.addPlayer("User", Color.WHITE);
            playerManager.addPlayer("Computer", Color.BLACK);
            playerManager.setCurrentPlayer(playerManager.getPlayers().get(0));

            this.dispose();
        });
        dualPlayerButton = new JButton("Dual Player");
        add(dualPlayerButton);
        dualPlayerButton.addActionListener(e -> {
            userPanel.setVisible(true);
            opponentPanel.setVisible(true);
            revalidate();
            repaint();
        });




    }
}
