package game.strategy;

import game.GameUtils;
import game.object.ChessGameState;
import game.factory.ChessPiece;
import game.Position;
import game.object.GameStatusListener;

import java.util.ArrayList;
import java.util.List;

public class KingStrategy implements MoveStrategy {
    @Override
    public List<Position> calculateMoves(GameStatusListener chessBoard, ChessPiece piece, GameUtils utils) {
        List<Position> validMoves = new ArrayList<>();
        int x = piece.getPosition().x();
        int y = piece.getPosition().y();

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

            if(isValidMove(newPosition, piece, chessBoard, utils)){
                validMoves.add(newPosition);
            }
        }

        return validMoves;
    }
}
