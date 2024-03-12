package game.factory;

import game.object.ChessPiece;
import game.core.Player;
import game.Position;

public class KnightFactory implements ChessPieceFactory {
    private static KnightFactory instance;

    public KnightFactory() {
    }

    public static synchronized KnightFactory getInstance() {
        if (instance == null) {
            instance = new KnightFactory();
        }
        return instance;
    }

    @Override
    public ChessPiece createChessPiece(Position position, Player.Color color) {
        return new ChessPiece(ChessPiece.Type.KNIGHT, position, color);
    }
}
