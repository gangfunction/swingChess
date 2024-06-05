package game.strategy.calculator;

import game.GameUtils;
import game.Position;
import game.factory.ChessPiece;
import game.object.GameStatusListener;
import game.strategy.MoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class DiagonalMoveCalculator implements MoveStrategy {
    @Override
    public List<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece chessPiece, GameUtils gameUtils) {
        List<Position> validMoves = new ArrayList<>();
        Position position = chessPiece.getPosition();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] direction : directions) {
            addMovesInDirection(validMoves, position, direction, chessGameState, chessPiece, gameUtils);
        }
        return validMoves;
    }

    private void addMovesInDirection(List<Position> validMoves, Position startPosition, int[] direction, GameStatusListener chessGameState, ChessPiece chessPiece, GameUtils gameUtils) {
        int x = startPosition.x();
        int y = startPosition.y();

        while (true) {
            x += direction[0];
            y += direction[1];
            Position newPosition = new Position(x, y);

            if (!gameUtils.isValidPosition(newPosition)) {
                break;
            }

            if (gameUtils.isPositionEmpty(newPosition, chessGameState)) {
                // 빈 위치인 경우 유효한 이동으로 추가
                validMoves.add(newPosition);
            } else {
                if (gameUtils.isPositionOccupiedByOpponent(newPosition, chessPiece.getColor(), chessGameState)) {
                    validMoves.add(newPosition);
                }
                break;
            }
        }
    }
}