package game.object;

import game.Position;
import game.core.Color;
import game.factory.ChessPiece;

import java.util.List;

public interface GameLogicActions {
    void handleSquareClick(int x, int y);
    List<Position> calculateMovesForPiece(ChessPiece piece);

    List<Position> getPositions(ChessPiece piece, Position clickedPosition);

    boolean isKingInCheck(Color color);

    void updateGameStateAfterMove(ChessPiece king, Position kingTargetPosition);

    void setAfterCastling(boolean b);

    void setQueenCastleSide(boolean b);
}
