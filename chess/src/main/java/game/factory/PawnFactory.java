package game.factory;

import game.object.ChessPiece;
import game.core.Player;
import game.Position;

public class PawnFactory extends AbstractChessPieceFactory{
    @Override
    protected ChessPiece.Type getType() {
        return ChessPiece.Type.PAWN;
    }

    private static PawnFactory instance;

    public PawnFactory() {}

    public static synchronized PawnFactory getInstance() {
        if (instance == null) {
            instance = new PawnFactory();
        }
        return instance;
    }
}
