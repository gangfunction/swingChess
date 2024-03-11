package hello.factory;

import hello.gameobject.ChessPiece;
import hello.core.Player;
import hello.Position;

public class BishopFactory implements ChessPieceFactory{
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
    public ChessPiece createChessPiece(Position position, Player.Color color) {
        return new ChessPiece(ChessPiece.Type.BISHOP, position, color);
    }
}
