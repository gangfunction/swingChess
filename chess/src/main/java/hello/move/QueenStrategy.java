package hello.move;

import hello.ChessBoard;
import hello.ChessPiece;
import hello.Position;

import java.util.ArrayList;
import java.util.List;

public class QueenStrategy implements MoveStrategy {
    @Override
    public List<Position> calculateMoves(ChessBoard chessBoard, ChessPiece piece) {
        List<Position> validMoves = new ArrayList<>();
        int[][] directions = {
                {1, 0}, {-1, 0}, // 상하
                {0, 1}, {0, -1}, // 좌우
                {1, 1}, {1, -1}, // 대각선
                {-1, 1}, {-1, -1}
        };

        for (int[] direction : directions) {
            int x = piece.getPosition().getX();
            int y = piece.getPosition().getY();

            while (true) {
                x += direction[0];
                y += direction[1];

                Position newPosition = new Position(x, y);

                if (!chessBoard.isValidPosition(newPosition)) {
                    break;
                }

                if (chessBoard.isPositionEmpty(newPosition)) {
                    validMoves.add(newPosition);
                } else {
                    if (chessBoard.isPositionOccupiedByOpponent(newPosition, piece.getColor())) {
                        validMoves.add(newPosition);
                    }
                    break;
                }
            }
        }

        return validMoves;
    }
}