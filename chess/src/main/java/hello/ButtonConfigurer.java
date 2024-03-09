package hello;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ButtonConfigurer {

    // 버튼 생성 및 패널에 추가하는 일반화된 메서드
    static JButton configureButton(String buttonText, ActionListener actionListener) {
        JButton button = new JButton(buttonText);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(actionListener);
        return button;
    }

}
