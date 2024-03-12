package game.strategy;

import game.GameUtils;
import game.Position;
import game.core.Player;
import game.object.ChessGameState;
import game.object.ChessPiece;

import java.util.ArrayList;
import java.util.List;

public class BishopStrategy implements MoveStrategy {

    public BishopStrategy() {

    }

    @Override
    public List<Position> calculateMoves(ChessGameState chessGameState, ChessPiece piece, GameUtils utils) {
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

                if (!utils.isValidPosition(nextPosition)) {
                    break;
                }

                if (utils.isPositionEmpty(nextPosition, chessGameState) ||
                        utils.isPositionOccupiedByOpponent(nextPosition, color, chessGameState)) {
                    validMoves.add(nextPosition);
                    if (utils.isPositionOccupiedByOpponent(nextPosition, color, chessGameState)) {
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
