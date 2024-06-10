package game.core.factory;

import game.Position;
import game.util.Color;
import game.util.PieceType;

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
