package game.factory;

import game.object.ChessPiece;
import game.core.Player;
import game.Position;

public class BishopFactory extends AbstractChessPieceFactory {
    private static BishopFactory instance;

    public BishopFactory() {
    }

    public static synchronized BishopFactory getInstance() {
        if (instance == null) {
            instance = new BishopFactory();
        }
        return instance;
    }


    @Override
    protected ChessPiece.Type getType() {
        return ChessPiece.Type.BISHOP;
    }
}

