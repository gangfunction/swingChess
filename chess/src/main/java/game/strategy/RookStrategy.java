package game.strategy;

import game.factory.ChessPiece;
import game.Position;
import game.object.GameStatusListener;
import game.strategy.calculator.StraightMoveCalculator;

import java.util.Set;

public class RookStrategy implements MoveStrategy {
    @Override
    public Set<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece chessPiece) {
        return new StraightMoveCalculator().calculateMoves(chessGameState, chessPiece);
    }
}
