package game.strategy.calculator;

import game.GameUtils;
import game.Position;
import game.factory.ChessPiece;
import game.object.GameStatusListener;
import game.strategy.MoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class StraightMoveCalculator implements MoveStrategy {
    @Override
    public List<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece chessPiece, GameUtils gameUtils) {
        List<Position> validMoves = new ArrayList<>();
        Position position = chessPiece.getPosition();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}; // 상, 하, 좌, 우 이동

        for (int[] direction : directions) {
            int x = position.x();
            int y = position.y();

            // 다음 위치를 계산할 때마다 새로운 Position 객체를 생성하여 사용
            for (int step = 1; step < 8; step++) {
                int nextX = x + direction[0] * step;
                int nextY = y + direction[1] * step;
                if (calculateValidMoves(chessGameState, chessPiece, gameUtils, validMoves, nextX, nextY)) break;
            }
        }
        return validMoves;
    }

    public static boolean calculateValidMoves(GameStatusListener chessGameState, ChessPiece chessPiece, GameUtils gameUtils, List<Position> validMoves, int nextX, int nextY) {
        Position nextPosition = new Position(nextX, nextY);

        if (!gameUtils.isValidPosition(nextPosition)) return true;

        if (gameUtils.isPositionEmpty(nextPosition, chessGameState) || gameUtils.isPositionOccupiedByOpponent(nextPosition, chessPiece.getColor(), chessGameState)) {
            validMoves.add(nextPosition);
            return gameUtils.isPositionOccupiedByOpponent(nextPosition, chessPiece.getColor(), chessGameState);
        } else {
            return true;
        }
    }
}
