package game.strategy.calculator;

import game.GameUtils;
import game.Position;
import game.object.ChessGameState;
import game.factory.ChessPiece;
import game.object.GameStatusListener;
import game.strategy.MoveCalculator;

import java.util.ArrayList;
import java.util.List;

public class DiagonalMoveCalculator implements MoveCalculator {
    @Override
    public List<Position> calculate(GameStatusListener chessGameState, ChessPiece chessPiece, GameUtils gameUtils) {
        List<Position> validMoves = new ArrayList<>();
        Position position = chessPiece.getPosition();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] direction : directions) {
            int x = position.x();
            int y = position.y();

            do {
                x += direction[0];
                y += direction[1];
            } while (!StraightMoveCalculator.calculateValidMoves(chessGameState, chessPiece, gameUtils, validMoves, x, y));
        }
        return validMoves;
    }
}
