package game.strategy;


import game.Position;
import game.factory.ChessPiece;
import game.object.GameStatusListener;
import game.strategy.calculator.DiagonalMoveCalculator;

import java.util.Set;

public class BishopStrategy implements MoveStrategy {
    @Override
    public Set<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece piece ) {
        return new DiagonalMoveCalculator().calculateMoves(chessGameState, piece);
    }
}
