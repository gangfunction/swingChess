package game.factory;

import game.object.ChessPiece;
import game.core.Player;
import game.Position;

public class RookFactory implements ChessPieceFactory{
    private static RookFactory instance;

    public RookFactory() {
    }

    public static synchronized RookFactory getInstance() {
        if (instance == null) {
            instance = new RookFactory();
        }
        return instance;
    }
    @Override
    public ChessPiece createChessPiece(Position position, Player.Color color) {
        return new ChessPiece(ChessPiece.Type.ROOK, position, color);
    }
}
