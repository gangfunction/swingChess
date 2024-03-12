package game.factory;

import game.object.ChessPiece;
import game.core.Player;
import game.Position;

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
