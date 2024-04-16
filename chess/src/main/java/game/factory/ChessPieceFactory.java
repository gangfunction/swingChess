package game.factory;

import game.core.Color;
import game.Position;

public interface ChessPieceFactory {
    ChessPiece createChessPiece(PieceType pieceType, Position position, Color color);
}
