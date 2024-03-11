package hello.factory;

import hello.gameobject.ChessPiece;
import hello.core.Player;
import hello.Position;

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
