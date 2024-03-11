package hello.move;

import hello.gameobject.ChessBoard;
import hello.gameobject.ChessPiece;
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

                if (!chessBoard.getDistanceManager().isValidPosition(newPosition, chessBoard)) {
                    break;
                }

                if (chessBoard.getDistanceManager().isPositionEmpty(newPosition, chessBoard)) {
                    validMoves.add(newPosition);
                } else {
                    if (chessBoard.getDistanceManager().isPositionOccupiedByOpponent(newPosition, piece.getColor(), chessBoard)) {
                        validMoves.add(newPosition);
                    }
                    break;
                }
            }
        }

        return validMoves;
    }
}