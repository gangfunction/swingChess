package hello;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ChessGameLauncher {

    public static void createAndShowGUI(JFrame owner) {
        // 메인 프레임 생성
        final JDialog dialog = new JDialog(owner, "체스 게임", true);
        dialog.setSize(300, 200);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // 컨테이너 패널 생성 및 레이아웃 설정
        JPanel panel = new JPanel();
        dialog.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // 버튼을 ButtonConfigurer를 사용하여 구성
        newGame(owner, panel, dialog);

        JButton loadGameButton = ButtonConfigurer.configureButton("게임 불러오기", e -> {
            System.out.println("게임 불러오기!");
            // 게임 불러오기 로직
        });
        panel.add(loadGameButton);

        JButton exitButton = ButtonConfigurer.configureButton("게임 종료", e -> System.exit(0));
        panel.add(exitButton);
        owner.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                dialog.setLocationRelativeTo(owner);
            }
        });

        // 프레임 가운데 정렬 및 표시
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private static void newGame(JFrame owner, JPanel panel, JDialog dialog) {
        ChessBoard chessBoard = new ChessBoard(); // 체스보드를 새로 생성하거나 초기화
        GameState gameState = new GameState(); // 게임 상태를 새로 생성하거나 초기화
        JButton newGameButton = ButtonConfigurer.configureButton("새 게임", e -> {
            try{
                dialog.setVisible(false);
                chessBoard.resetBoard();
                gameState.resetGame(); // 게임 상태를 새 게임 상태로 리셋
                boolean loadSuccess = true;
                if(!loadSuccess){
                    throw new Exception("게임 로딩 실패");
                }

            }catch (Exception ex){
                JOptionPane.showMessageDialog(dialog, "게임 로딩 실패. 3초 후 게임을 종료합니다.");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                System.exit(0);
            }
            System.out.println("새 게임 시작!");

            owner.setContentPane(chessBoard.getBoardPanel()); // 메인 프레임의 컨텐트 팬을 새 체스보드로 설정
            owner.revalidate(); // 메인 프레임의 UI를 새롭게 그립니다.
            owner.repaint();
        });
        panel.add(newGameButton);
    }


}
