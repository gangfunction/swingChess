package game.strategy;

import game.GameUtils;
import game.Position;
import game.core.factory.ChessPiece;
import game.model.state.ChessPieceManager;
import game.model.state.MoveManager;

import java.util.Set;

@FunctionalInterface
public interface MoveStrategy {
    Set<Position> calculateMoves(ChessPieceManager chessPieceManager, MoveManager moveManager, ChessPiece chessPiece);
    default boolean isValidMove(Position position, ChessPiece piece, ChessPieceManager chessPieceManager) {
        return GameUtils.isValidPosition(position) &&
                (GameUtils.isPositionEmpty(position, chessPieceManager) ||
                        GameUtils.isPositionOccupiedByOpponent(position, piece.getColor(), chessPieceManager));
    }
}
