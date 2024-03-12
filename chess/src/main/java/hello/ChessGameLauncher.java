package hello;

import hello.ui.ButtonConfigurer;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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

        // 버튼 추가 로직
        List<ButtonConfig> buttons = new ArrayList<>();
        buttons.add(new ButtonConfig("새 게임", e -> startNewGame(dialog)));
        buttons.add(new ButtonConfig("게임 불러오기", e -> loadGame(dialog)));
        buttons.add(new ButtonConfig("게임 종료", e -> System.exit(0)));

        addButtonsToPanel(panel, buttons);
        // 다이얼로그 위치 설정과 표시
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }
    private static void addButtonsToPanel(JPanel panel,  List<ButtonConfig> buttons) {
        buttons.forEach(buttonConfig -> panel.add(ButtonConfigurer.configureButton(buttonConfig.text(), buttonConfig.actionListener())));
    }

    private static void loadGame(JDialog dialog){
        System.out.println("게임 불러오기!");
        dialog.dispose();
    }

    private static void startNewGame(JDialog dialog) {
        System.out.println("새 게임 시작!");
        dialog.dispose();
        // TODO: 새 게임 시작 로직
    }

    public record ButtonConfig(String text, ActionListener actionListener) {
    }

}
