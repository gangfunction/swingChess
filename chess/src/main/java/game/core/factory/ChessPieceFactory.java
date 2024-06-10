package game.core.factory;

import game.util.Color;
import game.Position;
import game.util.PieceType;

public interface ChessPieceFactory {
    ChessPiece createChessPiece(PieceType pieceType, Position position, Color color);
}
