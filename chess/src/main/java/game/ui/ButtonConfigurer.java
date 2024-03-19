package game.ui;

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

}
