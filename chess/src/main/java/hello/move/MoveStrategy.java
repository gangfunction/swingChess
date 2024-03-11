package hello.move;

import hello.gameobject.ChessBoard;
import hello.gameobject.ChessPiece;
import hello.Position;

import java.util.List;

public interface MoveStrategy {
    List<Position> calculateMoves(ChessBoard manager, ChessPiece chessPiece);
}
