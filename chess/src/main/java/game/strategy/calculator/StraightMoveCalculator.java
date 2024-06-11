package game.strategy.calculator;

import game.GameUtils;
import game.Position;
import game.core.factory.ChessPiece;
import game.model.state.ChessPieceManager;
import game.model.state.MoveManager;
import game.strategy.MoveStrategy;

import java.util.HashSet;
import java.util.Set;

public class StraightMoveCalculator implements MoveStrategy {
    @Override
    public Set<Position> calculateMoves(ChessPieceManager chessGameState, MoveManager moveManager, ChessPiece chessPiece) {
        Set<Position> validMoves = new HashSet<>();
        Position position = chessPiece.getPosition();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}; // 상, 하, 좌, 우 이동

        for (int[] direction : directions) {
            int x = position.x();
            int y = position.y();

            for (int step = 1; step < 8; step++) {
                int nextX = x + direction[0] * step;
                int nextY = y + direction[1] * step;
                if (calculateValidMoves(chessGameState, chessPiece,  validMoves, nextX, nextY)) break;
            }
        }
        return validMoves;
    }

    public static boolean calculateValidMoves(ChessPieceManager chessPieceManger, ChessPiece chessPiece, Set<Position> validMoves, int nextX, int nextY) {
        Position nextPosition = new Position(nextX, nextY);

        if (!GameUtils.isValidPosition(nextPosition)) return true;

        if (GameUtils.isPositionEmpty(nextPosition, chessPieceManger) || GameUtils.isPositionOccupiedByOpponent(nextPosition, chessPiece.getColor(), chessPieceManger)) {
            validMoves.add(nextPosition);
            return GameUtils.isPositionOccupiedByOpponent(nextPosition, chessPiece.getColor(), chessPieceManger);
        } else {
            return true;
        }
    }
}
