package hello.move;

import hello.ChessBoard;
import hello.ChessPiece;
import hello.Player;
import hello.Position;

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

                if (!chessBoard.isValidPosition(nextPosition)) {
                    break;
                }

                if (chessBoard.isPositionEmpty(nextPosition) || chessBoard.isPositionOccupiedByOpponent(nextPosition, color)) {
                    validMoves.add(nextPosition);
                    if (chessBoard.isPositionOccupiedByOpponent(nextPosition, color)) {
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
