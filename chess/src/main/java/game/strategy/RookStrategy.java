package game.strategy;

import game.GameUtils;
import game.object.ChessGameState;
import game.factory.ChessPiece;
import game.Position;
import game.object.GameStatusListener;
import game.strategy.calculator.StraightMoveCalculator;

import java.util.List;
//TODO: 캐슬링 조건 구현
public class RookStrategy implements MoveStrategy {

    public RookStrategy() {
    }
    private final MoveCalculator straightMoveCalculator = new StraightMoveCalculator();

    @Override
    public List<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece chessPiece, GameUtils gameUtils) {
        return straightMoveCalculator.calculate(chessGameState, chessPiece, gameUtils);
    }
}
