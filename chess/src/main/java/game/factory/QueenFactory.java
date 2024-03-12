package game.factory;

import game.object.ChessPiece;
import game.core.Player;
import game.Position;

public class QueenFactory implements ChessPieceFactory{
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
    public ChessPiece createChessPiece(Position position, Player.Color color) {
        return new ChessPiece(ChessPiece.Type.QUEEN, position, color);
    }
}
