package game.factory;

import game.object.ChessPiece;

public class KnightFactory extends AbstractChessPieceFactory{
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
    protected ChessPiece.Type getType() {
        return ChessPiece.Type.KNIGHT;
    }

}
