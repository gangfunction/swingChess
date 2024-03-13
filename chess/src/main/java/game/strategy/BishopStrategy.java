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

    static final MoveCalculator diagonalMoveCalculator = (chessGameState, chessPiece, gameUtils) -> {
        List<Position> validMoves = new ArrayList<>();
        Position position = chessPiece.getPosition();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] direction : directions) {
            int x = position.getX();
            int y = position.getY();

            while (true) {
                x += direction[0];
                y += direction[1];
                Position nextPosition = new Position(x, y);

                if (!gameUtils.isValidPosition(nextPosition)) {
                    break;
                }

                if (gameUtils.isPositionEmpty(nextPosition, chessGameState) ||
                        gameUtils.isPositionOccupiedByOpponent(nextPosition, chessPiece.getColor(), chessGameState)) {
                    validMoves.add(nextPosition);
                    if (gameUtils.isPositionOccupiedByOpponent(nextPosition, chessPiece.getColor(), chessGameState)) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return validMoves;
    };

    @Override
    public List<Position> calculateMoves(ChessGameState chessGameState, ChessPiece piece, GameUtils utils) {
        return diagonalMoveCalculator.calculate(chessGameState, piece, utils);
    }
}
