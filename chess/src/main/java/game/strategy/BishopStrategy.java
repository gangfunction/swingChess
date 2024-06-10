package game.strategy;


import game.Position;
import game.core.factory.ChessPiece;
import game.model.GameStatusListener;
import game.strategy.calculator.DiagonalMoveCalculator;

import java.util.Set;

public class BishopStrategy implements MoveStrategy {
    private final DiagonalMoveCalculator diagonalMoveCalculator = new DiagonalMoveCalculator();
    @Override
    public Set<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece piece ) {
        return diagonalMoveCalculator.calculateMoves(chessGameState, piece);
    }
}
