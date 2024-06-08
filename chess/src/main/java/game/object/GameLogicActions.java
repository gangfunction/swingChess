package game.object;

import game.Position;
import game.core.Color;
import game.factory.ChessPiece;

import java.util.List;
import java.util.Set;

public interface GameLogicActions {
    void handleSquareClick(int x, int y);
    Set<Position> calculateMovesForPiece(ChessPiece piece);
    boolean isKingInCheck(Color color);
    void updateGameStateAfterMove(ChessPiece king, Position kingTargetPosition);
    boolean isKingInCheckAfterMove(ChessPiece piece, Position clickedPosition);
    void setAfterCastling(boolean b);
}
