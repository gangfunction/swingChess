package game;

import game.observer.ChessObserver;
import game.observer.Observer;

import javax.swing.*;
import java.awt.*;


public class GameLog implements Observer {
    private final JTextArea textArea;

    public GameLog(ChessObserver game) {
        game.addObserver(this);

        // 텍스트 영역 설정
        textArea = new JTextArea();
        textArea.setEditable(false);


        // 다이얼로그 설정
        JDialog dialog = createDialog();
        dialog.add(createScrollPane(textArea),BorderLayout.CENTER);
        dialog.pack();
        dialog.setVisible(true);
    }
    private JDialog createDialog(){
        JDialog dialog = new JDialog((Frame) null, "Game Log", false);
        dialog.setSize(400, 600);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(null);
        return dialog;
    }
    private JScrollPane createScrollPane(JTextArea textArea){
        return new JScrollPane(textArea);
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
