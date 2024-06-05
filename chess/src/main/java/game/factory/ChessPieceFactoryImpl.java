package game.factory;

import game.Position;
import game.core.Color;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum ChessPieceFactoryImpl implements ChessPieceFactory{
    INSTANCE;
    private static final Logger LOGGER = Logger.getLogger(ChessPieceFactoryImpl.class.getName());


    @Override
    public ChessPiece createChessPiece(PieceType pieceType, Position position, Color color) {
        LOGGER.log(Level.FINE, "Creating {0} {1} at {2}", new Object[]{color, pieceType, position});
        return new ChessPiece(pieceType, position, color);
    }
}
