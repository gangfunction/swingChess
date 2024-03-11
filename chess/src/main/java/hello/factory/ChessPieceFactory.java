package hello.factory;

import hello.gameobject.ChessPiece;
import hello.core.Player;
import hello.Position;

public interface ChessPieceFactory {
    ChessPiece createChessPiece(Position position, Player.Color color);
}
