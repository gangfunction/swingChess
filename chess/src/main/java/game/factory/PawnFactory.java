package game.factory;

import game.object.ChessPiece;
import game.core.Player;
import game.Position;

public class PawnFactory implements ChessPieceFactory{
    private static PawnFactory instance;

    public PawnFactory() {}

    public static synchronized PawnFactory getInstance() {
        if (instance == null) {
            instance = new PawnFactory();
        }
        return instance;
    }
    @Override
    public ChessPiece createChessPiece(Position position, Player.Color color) {
        return new ChessPiece(ChessPiece.Type.PAWN, position, color);
    }
}
