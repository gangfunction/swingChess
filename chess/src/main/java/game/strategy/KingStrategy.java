package game.strategy;

import game.GameUtils;
import game.object.ChessGameState;
import game.object.ChessPiece;
import game.Position;

import java.util.ArrayList;
import java.util.List;

public class KingStrategy implements MoveStrategy {

    public KingStrategy() {
    }

    @Override
    public List<Position> calculateMoves(ChessGameState chessBoard, ChessPiece piece, GameUtils utils) {
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
            if (utils.isValidPosition(newPosition) &&
                    (utils.isPositionEmpty(newPosition, chessBoard) ||
                            utils.isPositionOccupiedByOpponent(newPosition, piece.getColor(), chessBoard))) {
                validMoves.add(newPosition);
            }
        }

        return validMoves;
    }
}
