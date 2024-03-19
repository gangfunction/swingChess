package game.strategy;

import game.GameUtils;
import game.object.ChessGameState;
import game.factory.ChessPiece;
import game.Position;
import game.strategy.calculator.DiagonalMoveCalculator;
import game.strategy.calculator.StraightMoveCalculator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueenStrategy implements MoveStrategy {
    public QueenStrategy() {

    }

    private final MoveCalculator diagonalMoveCalculator = new DiagonalMoveCalculator();
    private final MoveCalculator straightMoveCalculator = new StraightMoveCalculator();
    @Override
    public List<Position> calculateMoves(ChessGameState chessGameState, ChessPiece chessPiece, GameUtils utils) {

        List<Position> diagonalMoves = diagonalMoveCalculator.calculate(chessGameState, chessPiece, utils);
        // 수직/수평 이동 계산
        List<Position> straightMoves = straightMoveCalculator.calculate(chessGameState, chessPiece, utils);

        // 대각선 이동과 수직/수평 이동 결과를 합칩니다.
        return Stream.concat(diagonalMoves.stream(), straightMoves.stream())
                .collect(Collectors.toList());
    }
}