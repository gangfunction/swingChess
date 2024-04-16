package game.factory;

import game.Position;
import game.core.Color;

import java.util.logging.Logger;

public enum ChessPieceFactoryImpl implements ChessPieceFactory{
    INSTANCE;
    private static final Logger LOGGER = Logger.getLogger(ChessPieceFactoryImpl.class.getName());

    ChessPieceFactoryImpl() {
    }

    @Override
    public ChessPiece createChessPiece(PieceType pieceType, Position position, Color color) {
        LOGGER.fine("Creating " + color + " " + pieceType + " at " + position);
        return new ChessPiece(pieceType, position, color);
    }
}
