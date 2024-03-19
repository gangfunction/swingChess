package game.strategy;

import game.GameUtils;
import game.Position;
import game.object.ChessGameState;
import game.factory.ChessPiece;

import java.util.List;

@FunctionalInterface
public interface MoveCalculator {
    List<Position> calculate(ChessGameState chessGameState, ChessPiece piece, GameUtils utils);
}
