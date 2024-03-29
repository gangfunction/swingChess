package game.strategy;

import game.GameUtils;
import game.Position;
import game.object.ChessGameState;
import game.factory.ChessPiece;
import game.object.GameStatusListener;
import game.strategy.calculator.DiagonalMoveCalculator;

import java.util.List;

public class BishopStrategy implements MoveStrategy {

    public BishopStrategy() {

    }


    @Override
    public List<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece piece, GameUtils utils) {
        return new DiagonalMoveCalculator().calculate(chessGameState, piece, utils);
    }
}
