package game.strategy;

import game.core.factory.ChessPiece;
import game.Position;
import game.model.state.ChessPieceManager;
import game.model.state.MoveManager;
import game.strategy.calculator.DiagonalMoveCalculator;
import game.strategy.calculator.StraightMoveCalculator;

import java.util.HashSet;
import java.util.Set;

public class QueenStrategy implements MoveStrategy {
    private final DiagonalMoveCalculator diagonalMoveCalculator = new DiagonalMoveCalculator();
    private final StraightMoveCalculator straightMoveCalculator = new StraightMoveCalculator();

    @Override
    public Set<Position> calculateMoves(ChessPieceManager chessPieceManager, MoveManager moveManager, ChessPiece chessPiece) {
        Set<Position> moves = new HashSet<>();
        moves.addAll(diagonalMoveCalculator.calculateMoves(chessPieceManager,moveManager, chessPiece));
        moves.addAll(straightMoveCalculator.calculateMoves(chessPieceManager,moveManager, chessPiece));
        return moves;
    }
}