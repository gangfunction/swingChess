package hello.factory;

import hello.gameobject.ChessPiece;
import hello.core.Player;
import hello.Position;

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
