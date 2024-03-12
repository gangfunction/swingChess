package hello.strategy;

import hello.GameUtils;
import hello.Position;
import hello.gameobject.ChessGameState;
import hello.gameobject.ChessPiece;

import java.util.List;

public interface MoveStrategy {
    List<Position> calculateMoves(ChessGameState chessGameState, ChessPiece chessPiece, GameUtils utils);
}
