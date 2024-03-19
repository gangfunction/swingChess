package game.object;

import game.Position;
import game.factory.ChessPiece;

import java.util.List;

public interface GameLogicActions {
    void handleSquareClick(int x, int y);
    List<Position> calculateMovesForPiece(ChessPiece piece);
}
