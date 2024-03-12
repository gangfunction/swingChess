package game.strategy;

import game.GameUtils;
import game.Position;
import game.object.ChessGameState;
import game.object.ChessPiece;

import java.util.List;

public interface MoveStrategy {
    List<Position> calculateMoves(ChessGameState chessGameState, ChessPiece chessPiece, GameUtils utils);
}
