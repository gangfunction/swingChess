package game.strategy;

import game.GameUtils;
import game.Position;
import game.core.factory.ChessPiece;
import game.model.GameStatusListener;

import java.util.Set;

@FunctionalInterface
public interface MoveStrategy {
    Set<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece chessPiece);
    default boolean isValidMove(Position position, ChessPiece piece, GameStatusListener chessGameState) {
        return GameUtils.isValidPosition(position) &&
                (GameUtils.isPositionEmpty(position, chessGameState) ||
                        GameUtils.isPositionOccupiedByOpponent(position, piece.getColor(), chessGameState));
    }
}
