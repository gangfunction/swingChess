package game.factory;

import game.object.ChessPiece;
import game.core.Player;
import game.Position;

public interface ChessPieceFactory {
    ChessPiece createChessPiece(Position position, Player.Color color);
}
