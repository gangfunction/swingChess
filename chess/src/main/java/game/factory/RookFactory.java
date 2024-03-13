package game.factory;

import game.object.ChessPiece;
import game.core.Player;
import game.Position;

public class RookFactory extends AbstractChessPieceFactory{
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
    protected ChessPiece.Type getType() {
        return ChessPiece.Type.ROOK;
    }

}
