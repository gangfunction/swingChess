package game.strategy;

import game.GameUtils;
import game.object.ChessGameState;
import game.factory.ChessPiece;
import game.Position;
import game.object.GameStatusListener;
import game.strategy.calculator.DiagonalMoveCalculator;
import game.strategy.calculator.StraightMoveCalculator;

import javax.tools.DiagnosticCollector;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueenStrategy implements MoveStrategy {
    @Override
    public List<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece chessPiece, GameUtils utils) {

        List<Position> diagonalMoves =  new DiagonalMoveCalculator().calculateMoves(chessGameState, chessPiece, utils);
        // 수직/수평 이동 계산
        List<Position> straightMoves = new StraightMoveCalculator().calculateMoves(chessGameState, chessPiece, utils);

        // 대각선 이동과 수직/수평 이동 결과를 합칩니다.
        return Stream.concat(diagonalMoves.stream(), straightMoves.stream())
                .collect(Collectors.toList());
    }
}