package game.model;

import game.Position;
import game.util.Color;
import game.core.factory.ChessPiece;

import java.util.Set;

public interface GameLogicActions {
    void handleSquareClick(int x, int y);
    Set<Position> calculateMovesForPiece(ChessPiece piece);
    boolean isKingInCheck(Color color);
    void updateGameStateAfterMove(ChessPiece king, Position kingTargetPosition);
    boolean isKingInCheckAfterMove(ChessPiece piece, Position clickedPosition);
    void setAfterCastling(boolean b);

    boolean isFriendlyPieceAtPosition(Position position, ChessPiece thisPiece);
    void executeMove(ChessPiece selectedPiece, Position clickedPosition);

    void moveActions(ChessPiece pieceAtPosition, Position toPosition);
}
