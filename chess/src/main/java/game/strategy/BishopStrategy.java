package game.strategy;

import game.GameUtils;
import game.Position;
import game.object.ChessGameState;
import game.factory.ChessPiece;
import game.strategy.calculator.DiagonalMoveCalculator;

import java.util.List;

public class BishopStrategy implements MoveStrategy {

    public BishopStrategy() {

    }

    private final MoveCalculator diagonalMoveCalculator = new DiagonalMoveCalculator();

    @Override
    public List<Position> calculateMoves(ChessGameState chessGameState, ChessPiece piece, GameUtils utils) {
        return diagonalMoveCalculator.calculate(chessGameState, piece, utils);
    }
}
