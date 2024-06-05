package game.strategy;

import game.GameUtils;
import game.Position;
import game.object.ChessGameState;
import game.factory.ChessPiece;
import game.object.GameStatusListener;

import java.util.List;
@FunctionalInterface
public interface MoveStrategy {
    List<Position> calculateMoves(GameStatusListener chessGameState, ChessPiece chessPiece, GameUtils utils);
    default boolean isValidMove(Position position, ChessPiece piece, GameStatusListener chessGameState, GameUtils utils) {
        return utils.isValidPosition(position) &&
                (utils.isPositionEmpty(position, chessGameState) ||
                        utils.isPositionOccupiedByOpponent(position, piece.getColor(), chessGameState));
    }
}
