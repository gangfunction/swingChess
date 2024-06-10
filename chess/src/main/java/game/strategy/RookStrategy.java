package game.strategy;

import game.core.factory.ChessPiece;
import game.Position;
import game.model.GameStatusListener;
import game.strategy.calculator.StraightMoveCalculator;

import java.util.Set;

public class RookStrategy implements MoveStrategy {
    private final StraightMoveCalculator straightMoveCalculator = new StraightMoveCalculator();
    @Override
    public Set<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece chessPiece) {
        return straightMoveCalculator.calculateMoves(chessGameState, chessPiece);
    }
}
