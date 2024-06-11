package game.strategy;

import game.core.factory.ChessPiece;
import game.Position;
import game.model.state.ChessPieceManager;
import game.model.state.MoveManager;
import game.strategy.calculator.StraightMoveCalculator;

import java.util.Set;

public class RookStrategy implements MoveStrategy {
    private final StraightMoveCalculator straightMoveCalculator = new StraightMoveCalculator();
    @Override
    public Set<Position> calculateMoves(ChessPieceManager chessPieceManager, MoveManager moveManager, ChessPiece chessPiece) {
        return straightMoveCalculator.calculateMoves(chessPieceManager,moveManager, chessPiece);
    }
}
