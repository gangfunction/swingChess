package hello.move;

import hello.*;
import hello.core.Player;
import hello.gameobject.ChessBoard;
import hello.gameobject.ChessPiece;

import java.util.ArrayList;
import java.util.List;

public class BishopStrategy implements MoveStrategy {
    @Override
    public List<Position> calculateMoves(ChessBoard chessBoard, ChessPiece piece) {
        List<Position> validMoves = new ArrayList<>();
        Position position = piece.getPosition();
        Player.Color color = piece.getColor();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] direction : directions) {
            int x = position.getX();
            int y = position.getY();

            while (true) {
                x += direction[0];
                y += direction[1];
                Position nextPosition = new Position(x, y);

                if (!chessBoard.getDistanceManager().isValidPosition(nextPosition, chessBoard)) {
                    break;
                }

                if (chessBoard.getDistanceManager().isPositionEmpty(nextPosition, chessBoard) ||
                        chessBoard.getDistanceManager().isPositionOccupiedByOpponent(nextPosition, color, chessBoard)) {
                    validMoves.add(nextPosition);
                    if (chessBoard.getDistanceManager().isPositionOccupiedByOpponent(nextPosition, color, chessBoard)) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        return validMoves;
    }
}
