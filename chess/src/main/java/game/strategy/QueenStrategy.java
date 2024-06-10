package game.strategy;

import game.core.factory.ChessPiece;
import game.Position;
import game.model.GameStatusListener;
import game.strategy.calculator.DiagonalMoveCalculator;
import game.strategy.calculator.StraightMoveCalculator;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueenStrategy implements MoveStrategy {
    private final DiagonalMoveCalculator diagonalMoveCalculator = new DiagonalMoveCalculator();
    private final StraightMoveCalculator straightMoveCalculator = new StraightMoveCalculator();

    @Override
    public Set<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece chessPiece) {
        Set<Position> moves = new HashSet<>();
        moves.addAll(diagonalMoveCalculator.calculateMoves(chessGameState, chessPiece));
        moves.addAll(straightMoveCalculator.calculateMoves(chessGameState, chessPiece));
        return moves;
    }
}