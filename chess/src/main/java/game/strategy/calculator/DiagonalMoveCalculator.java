package game.strategy.calculator;

import game.GameUtils;
import game.Position;
import game.factory.ChessPiece;
import game.object.GameStatusListener;
import game.strategy.MoveStrategy;

import java.util.HashSet;
import java.util.Set;

public class DiagonalMoveCalculator implements MoveStrategy {
    @Override
    public Set<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece chessPiece) {
        Set<Position> validMoves = new HashSet<>();
        Position position = chessPiece.getPosition();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] direction : directions) {
            addMovesInDirection(validMoves, position, direction, chessGameState, chessPiece);
        }
        return validMoves;
    }

    private void addMovesInDirection(Set<Position> validMoves, Position startPosition, int[] direction, GameStatusListener chessGameState, ChessPiece chessPiece) {
        int x = startPosition.x();
        int y = startPosition.y();

        while (true) {
            x += direction[0];
            y += direction[1];
            Position newPosition = new Position(x, y);

            if (!GameUtils.isValidPosition(newPosition)) {
                break;
            }

            if (GameUtils.isPositionEmpty(newPosition, chessGameState)) {
                validMoves.add(newPosition);
            } else {
                if (GameUtils.isPositionOccupiedByOpponent(newPosition, chessPiece.getColor(), chessGameState)) {
                    validMoves.add(newPosition);
                }
                break;
            }
        }
    }
}