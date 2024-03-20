package game;

import game.observer.ChessObserver;
import game.observer.Observer;

import javax.swing.*;
import java.awt.*;


public class GameLog implements Observer {
    private final JTextArea textArea;

    public GameLog(ChessObserver observer, JTextArea textArea) {
        this.textArea = textArea;
        observer.addObserver(this);
        // 텍스트 영역 설정
        textArea = new JTextArea();
        textArea.setEditable(false);


    }


    @Override
    public void update(String gameState) {
        appendLogEntry(gameState);
    }

    @Override
    public void logAction(String message) {
        appendLogEntry(message);
    }
    private void appendLogEntry(String message) {
        SwingUtilities.invokeLater(()->{
            textArea.append(message + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }


}
