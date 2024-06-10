package game.strategy;

import game.*;
import game.core.factory.ChessPiece;
import game.model.GameStatusListener;

import java.util.HashSet;
import java.util.Set;

public class KnightStrategy implements MoveStrategy {
    @Override
    public Set<Position> calculateMoves(GameStatusListener chessBoard, ChessPiece piece) {
        Set<Position> validMoves = new HashSet<>();
        Position position = piece.getPosition();
        int[][] directions = {
                {-2, -1}, {-2, 1},
                {-1, -2}, {-1, 2},
                {1, -2}, {1, 2},
                {2, -1}, {2, 1}
        };

        for (int[] dir : directions) {
            int newX = position.x() + dir[0];
            int newY = position.y() + dir[1];

            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                Position newPosition = new Position(newX, newY);
                if(isValidMove(newPosition, piece, chessBoard)){
                    validMoves.add(newPosition);
                }
            }
        }

        return validMoves;
    }
}
