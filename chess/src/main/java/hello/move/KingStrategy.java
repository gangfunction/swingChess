package hello.move;

import hello.gameobject.ChessBoard;
import hello.gameobject.ChessPiece;
import hello.Position;

import java.util.ArrayList;
import java.util.List;

public class KingStrategy implements MoveStrategy {

    @Override
    public List<Position> calculateMoves(ChessBoard chessBoard, ChessPiece piece) {
        List<Position> validMoves = new ArrayList<>();
        int x = piece.getPosition().getX();
        int y = piece.getPosition().getY();

        // 왕이 이동할 수 있는 모든 방향의 변화량
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1},  {1, 0},  {1, 1}
        };

        for (int[] direction : directions) {
            int newX = x + direction[0];
            int newY = y + direction[1];
            Position newPosition = new Position(newX, newY);

            // 새 위치가 유효한 위치인지 및 적의 말이 있는지 또는 비어 있는지 확인
            if (chessBoard.getDistanceManager().isValidPosition(newPosition, chessBoard) &&
                    (chessBoard.getDistanceManager().isPositionEmpty(newPosition, chessBoard) ||
                            chessBoard.getDistanceManager().isPositionOccupiedByOpponent(newPosition, piece.getColor(), chessBoard))) {
                validMoves.add(newPosition);
            }
        }

        return validMoves;
    }
}
