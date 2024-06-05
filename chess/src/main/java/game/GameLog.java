package game;

import game.observer.ChessObserver;
import game.observer.Observer;

import javax.swing.*;

public class GameLog implements Observer {
    private final JTextArea textArea;

    public GameLog(ChessObserver observer, JTextArea textArea) {
        this.textArea = textArea;
        this.textArea.setEditable(false);
        observer.addObserver(this);
    }

    @Override
    public void update(String gameState) {
        appendLogEntry(gameState);
    }

    private void appendLogEntry(String message) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(message + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }
}