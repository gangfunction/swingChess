package game.factory;

import game.Position;
import game.core.Player;
import game.object.ChessPiece;

public abstract class AbstractChessPieceFactory implements ChessPieceFactory{
    protected abstract ChessPiece.Type getType();
    @Override
    public ChessPiece createChessPiece(Position position, Player.Color color) {
        return new ChessPiece(getType(), position, color);
    }
}
