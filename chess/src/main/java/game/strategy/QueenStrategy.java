package game.strategy;

import game.factory.ChessPiece;
import game.Position;
import game.object.GameStatusListener;
import game.strategy.calculator.DiagonalMoveCalculator;
import game.strategy.calculator.StraightMoveCalculator;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueenStrategy implements MoveStrategy {
    @Override
    public Set<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece chessPiece) {
        Set<Position> diagonalMoves =  new DiagonalMoveCalculator().calculateMoves(chessGameState, chessPiece);
        Set<Position> straightMoves = new StraightMoveCalculator().calculateMoves(chessGameState, chessPiece);
        return  Stream.concat(diagonalMoves.stream(), straightMoves.stream())
                .collect(Collectors.toSet());
    }
}