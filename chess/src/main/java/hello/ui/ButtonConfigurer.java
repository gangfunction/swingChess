package hello.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ButtonConfigurer {

    public static JButton configureButton(String buttonText, ActionListener actionListener) {
        JButton button = new JButton(buttonText);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(actionListener);
        return button;
    }

    public static JButton configureButton(String buttonText, ActionListener actionListener, Dimension size, Font font, Color bgColor, Color fgColor) {
        JButton button = new JButton(buttonText);
        button.setMaximumSize(size);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(actionListener);
        return button;
    }
}
