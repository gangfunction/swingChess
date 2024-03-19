package game.factory;

import game.Position;
import game.core.Color;

import java.util.logging.Logger;

public enum ChessPieceFactoryImpl implements ChessPieceFactory{
    INSTANCE;
    private static final Logger LOGGER = Logger.getLogger(ChessPieceFactoryImpl.class.getName());

    @Override
    public ChessPiece createChessPiece(Type type, Position position, Color color) {
        LOGGER.fine("Creating " + color + " " + type + " at " + position);
        return new ChessPiece( type, position, color);
    }
}
