package game.strategy.calculator;

import game.GameUtils;
import game.Position;
import game.core.factory.ChessPiece;
import game.model.state.ChessPieceManager;
import game.model.state.MoveManager;
import game.strategy.MoveStrategy;

import java.util.HashSet;
import java.util.Set;

public class DiagonalMoveCalculator implements MoveStrategy {
    @Override
    public Set<Position> calculateMoves(ChessPieceManager chessPieceManager, MoveManager moveManager ,ChessPiece chessPiece) {
        Set<Position> validMoves = new HashSet<>();
        Position position = chessPiece.getPosition();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] direction : directions) {
            addMovesInDirection(validMoves, position, direction, chessPieceManager, chessPiece);
        }
        return validMoves;
    }

    private void addMovesInDirection(Set<Position> validMoves, Position startPosition, int[] direction, ChessPieceManager chessPieceManager, ChessPiece chessPiece) {
        int x = startPosition.x();
        int y = startPosition.y();

        while (true) {
            x += direction[0];
            y += direction[1];
            Position newPosition = new Position(x, y);

            if (!GameUtils.isValidPosition(newPosition)) {
                break;
            }

            if (GameUtils.isPositionEmpty(newPosition, chessPieceManager)) {
                validMoves.add(newPosition);
            } else {
                if (GameUtils.isPositionOccupiedByOpponent(newPosition, chessPiece.getColor(), chessPieceManager)) {
                    validMoves.add(newPosition);
                }
                break;
            }
        }
    }
}