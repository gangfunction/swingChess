package game.factory;

import game.object.ChessPiece;
import game.core.Player;
import game.Position;

public class KingFactory extends AbstractChessPieceFactory {
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
    protected ChessPiece.Type getType() {
        return ChessPiece.Type.KING;
    }

}
