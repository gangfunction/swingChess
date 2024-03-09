package hello;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ChessPieceIconLoader {
    public static Icon loadIcon(ChessPiece.Type type, Player.Color color) {
        String iconName = getIconName(type, color);
        URL iconURL = ChessPieceIconLoader.class.getResource(iconName);
        if (iconURL != null) {
            try {
                BufferedImage image = ImageIO.read(iconURL);
                if (image != null) {
                    Image resizedImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    return new ImageIcon(resizedImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Icon not found: " + iconName);
        }
        return null;
    }

    private static String getIconName(ChessPiece.Type type, Player.Color color) {
        System.out.println("type: " + type + ", color: " + color);
        String colorPrefix = (color == Player.Color.WHITE) ? "white" : "black";

        return "/icons/" + colorPrefix + "/" + type.toString().toLowerCase() + ".png";

    }
}
