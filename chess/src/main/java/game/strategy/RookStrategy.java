package game.strategy;

import game.GameUtils;
import game.object.ChessGameState;
import game.factory.ChessPiece;
import game.Position;
import game.object.GameStatusListener;
import game.strategy.calculator.StraightMoveCalculator;

import java.util.List;
public class RookStrategy implements MoveStrategy {

    public RookStrategy() {
    }

    @Override
    public List<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece chessPiece, GameUtils gameUtils) {
        return new StraightMoveCalculator().calculate(chessGameState, chessPiece, gameUtils);
    }
}
