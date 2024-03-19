package game.factory;

import game.core.Color;
import game.Position;

public interface ChessPieceFactory {
    ChessPiece createChessPiece(Type type, Position position, Color color);
}
