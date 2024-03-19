package game.ui;

import game.core.Color;
import game.factory.Type;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class IconLoader {
    private static final int ICON_WIDTH = 50;
    private static final int ICON_HEIGHT = 50;


    public static Icon loadIcon(Type type, Color color) {
        String iconName = getIconName(type, color);
        URL iconURL = IconLoader.class.getResource(iconName);
        if (iconURL != null) {
            return createIconFromURL(iconURL);
        } else {
            System.err.println("Icon not found: " + iconName);
            return null;
        }
    }

    private static Icon createIconFromURL(URL iconURL) {
        try {
            BufferedImage image = ImageIO.read(iconURL);
            if (image != null) {
                Image resizedImage = image.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
                return new ImageIcon(resizedImage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getIconName(Type type, Color color) {
        System.out.println("type: " + type + ", color: " + color);
        String colorPrefix = (color == Color.WHITE) ? "white" : "black";

        return "/icons/" + colorPrefix + "/" + type.toString().toLowerCase() + ".png";

    }
}
