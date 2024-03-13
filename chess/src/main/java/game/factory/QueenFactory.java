package game.factory;

import game.object.ChessPiece;
import game.core.Player;
import game.Position;

public class QueenFactory extends AbstractChessPieceFactory{
    private static QueenFactory instance;

    public QueenFactory() {
    }

    public static synchronized QueenFactory getInstance() {
        if (instance == null) {
            instance = new QueenFactory();
        }
        return instance;
    }

    @Override
    protected ChessPiece.Type getType() {
        return ChessPiece.Type.QUEEN;
    }

}
