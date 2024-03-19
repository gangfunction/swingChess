package game.factory;


import game.Position;
import game.core.Color;

import java.util.logging.Logger;

public class ChessPiece {
    private static final Logger LOGGER = Logger.getLogger(ChessPiece.class.getName());
    private Type type;
    private Position position;
    private Color color;
    private boolean moved = false;

    public boolean isMoved() {
        return !moved;
    }
    public void setMoved(boolean moved) {
        LOGGER.fine(this.type + "set to" + (moved ? "moved" : "not moved"));
        this.moved = moved;
    }
    public ChessPiece() {
    }

    public ChessPiece(Type type, Position position, Color color) {
        this.type = type;
        this.position = position;
        this.color = color;
    }

    public void setPosition(Position position) {
        LOGGER.fine("Setting position of " + this + " to " + position);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

    public Type getType() {
        return type;
    }
}