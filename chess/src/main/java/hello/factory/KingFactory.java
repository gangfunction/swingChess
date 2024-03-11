package hello.factory;

import hello.gameobject.ChessPiece;
import hello.core.Player;
import hello.Position;

public class KingFactory implements ChessPieceFactory{
    private static KingFactory instance;

    public KingFactory() {
    }

    public static synchronized KingFactory getInstance() {
        if (instance == null) {
            instance = new KingFactory();
        }
        return instance;
    }
    @Override
    public ChessPiece createChessPiece(Position position, Player.Color color) {
        return new ChessPiece(ChessPiece.Type.KING, position, color);
    }
}
