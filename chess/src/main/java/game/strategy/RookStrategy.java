package game.strategy;

import game.GameUtils;
import game.object.ChessGameState;
import game.object.ChessPiece;
import game.Position;

import java.util.ArrayList;
import java.util.List;

public class RookStrategy implements MoveStrategy {

    public RookStrategy() {
    }

    @Override
    public List<Position> calculateMoves(ChessGameState chessBoard, ChessPiece piece, GameUtils gameUtils) {
        List<Position> validMoves = new ArrayList<>();
        int x = piece.getPosition().getX();
        int y = piece.getPosition().getY();

        // 상하좌우 방향으로 이동 가능한 위치 계산
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] direction : directions) {
            int currentX = x;
            int currentY = y;
            while (true) {
                currentX += direction[0];
                currentY += direction[1];
                Position newPosition = new Position(currentX, currentY);

                if (!gameUtils.isValidPosition(newPosition)) {
                    break; // 보드 범위를 벗어난 경우
                }

                if (gameUtils.isPositionEmpty(newPosition, chessBoard) || gameUtils.isPositionOccupiedByOpponent(newPosition, piece.getColor(), chessBoard)) {
                    validMoves.add(newPosition); // 빈 칸이거나 상대 말이 있는 경우
                    if (gameUtils.isPositionOccupiedByOpponent(newPosition, piece.getColor(), chessBoard)) {
                        break; // 상대 말을 잡을 수 있는 위치에 도달한 경우
                    }
                } else {
                    break; // 자신의 말이 있는 위치에 도달한 경우
                }
            }
        }

        return validMoves;
    }
}
