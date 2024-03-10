package hello.move;

import hello.ChessBoard;
import hello.ChessPiece;
import hello.Position;

import java.util.List;

public interface MoveStrategy {
    List<Position> calculateMoves(ChessBoard chessBoard, ChessPiece chessPiece);
}
