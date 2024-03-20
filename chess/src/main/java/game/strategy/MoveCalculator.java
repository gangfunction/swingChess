package game.strategy;

import game.GameUtils;
import game.Position;
import game.object.ChessGameState;
import game.factory.ChessPiece;
import game.object.GameStatusListener;

import java.util.List;

@FunctionalInterface
public interface MoveCalculator {
    List<Position> calculate(GameStatusListener chessGameState, ChessPiece piece, GameUtils utils);
}
