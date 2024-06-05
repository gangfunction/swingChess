package game;

import game.ui.ButtonConfigurer;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ChessGameLauncher {

    public static void createAndShowGUI(JFrame owner) {
        JDialog dialog = createDialog(owner);
        JPanel panel = createPanel(dialog);

        List<ButtonConfig> buttons = createButtonConfigs(dialog);
        addButtonsToPanel(panel, buttons);

        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private static JDialog createDialog(JFrame owner) {
        JDialog dialog = new JDialog(owner, "체스 게임", true);
        dialog.setSize(300, 200);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        return dialog;
    }

    private static JPanel createPanel(JDialog dialog) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        dialog.add(panel);
        return panel;
    }

    private static List<ButtonConfig> createButtonConfigs(JDialog dialog) {
        List<ButtonConfig> buttons = new ArrayList<>();
        buttons.add(new ButtonConfig("새 게임", e -> startNewGame(dialog)));
        buttons.add(new ButtonConfig("게임 불러오기", e -> loadGame(dialog)));
        buttons.add(new ButtonConfig("게임 종료", e -> System.exit(0)));
        return buttons;
    }

    private static void addButtonsToPanel(JPanel panel, List<ButtonConfig> buttons) {
        buttons.forEach(buttonConfig -> {
            panel.add(ButtonConfigurer.configureButton(buttonConfig.text(), buttonConfig.actionListener()));
        });
    }

    private static void loadGame(JDialog dialog) {
        System.out.println("게임 불러오기!");
        dialog.dispose();
    }

    private static void startNewGame(JDialog dialog) {
        System.out.println("새 게임 시작!");
        dialog.dispose();
    }

    public record ButtonConfig(String text, ActionListener actionListener) {
    }
}