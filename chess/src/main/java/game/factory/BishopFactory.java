package game.factory;

import game.object.ChessPiece;
import game.core.Player;
import game.Position;

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
