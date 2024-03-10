package hello.move;

import hello.ChessBoard;
import hello.ChessPiece;
import hello.Player;
import hello.Position;

import java.util.ArrayList;
import java.util.List;

public class KnightStrategy implements MoveStrategy {

    @Override
    public List<Position> calculateMoves(ChessBoard chessBoard, ChessPiece piece) {
        List<Position> validMoves = new ArrayList<>();
        Position position = piece.getPosition();
        Player.Color color = piece.getColor();
        int[][] directions = {
                {-2, -1}, {-2, 1},
                {-1, -2}, {-1, 2},
                {1, -2}, {1, 2},
                {2, -1}, {2, 1}
        };

        for (int[] dir : directions) {
            int newX = position.getX() + dir[0];
            int newY = position.getY() + dir[1];

            // 이동하려는 위치가 체스판 내부에 있는지 확인
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                Position newPosition = new Position(newX, newY);

                // 이동하려는 위치에 자신의 말이 없는지 확인
                if (chessBoard.isPositionEmpty(newPosition)) {
                    validMoves.add(newPosition);
                }
            }
        }

        return validMoves;
    }


}
