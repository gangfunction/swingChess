package game;

import game.observer.ChessObserver;
import game.observer.Observer;

import javax.swing.*;
import java.awt.*;

public class GameLog implements Observer {
    private final ChessObserver game;
    private final JTextArea textArea;
    private final JDialog dialog;
    public GameLog(ChessObserver game) {
        this.game = game;
        game.addObserver(this);
        // 다이얼로그 설정
        dialog = new JDialog((Frame) null, "Game Log", false);
        dialog.setSize(400, 600);
        dialog.setLayout(new BorderLayout());

        // 텍스트 영역 설정
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // 다이얼로그 표시
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
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
        textArea.append(message + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }


}
