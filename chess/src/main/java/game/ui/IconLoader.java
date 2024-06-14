package game.ui;

import game.util.Color;
import game.util.PieceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class IconLoader {
    private static final int ICON_WIDTH = 50;
    private static final int ICON_HEIGHT = 50;
    private static final Logger log = LoggerFactory.getLogger(IconLoader.class);


    public static Icon loadIcon(PieceType pieceType, Color color) {
        String iconName = getIconName(pieceType, color);
        URL iconURL = IconLoader.class.getResource(iconName);
        if (iconURL != null) {
            return createIconFromURL(iconURL);
        } else {
            log.error("Failed to load icon: {}", iconName);
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
            log.error("Failed to load icon from URL: {}", iconURL, e);
        }
        return null;
    }

    private static String getIconName(PieceType pieceType, Color color) {
        String colorPrefix = (color == Color.WHITE) ? "white" : "black";

        return "/icons/" + colorPrefix + "/" + pieceType.toString().toLowerCase() + ".png";

    }

}
