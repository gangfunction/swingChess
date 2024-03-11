package hello.factory;

import hello.gameobject.ChessPiece;
import hello.core.Player;
import hello.Position;

public class RookFactory implements ChessPieceFactory{
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
    public ChessPiece createChessPiece(Position position, Player.Color color) {
        return new ChessPiece(ChessPiece.Type.ROOK, position, color);
    }
}
