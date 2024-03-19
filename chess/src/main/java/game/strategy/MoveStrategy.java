package game.strategy;

import game.GameUtils;
import game.Position;
import game.object.ChessGameState;
import game.factory.ChessPiece;

import java.util.List;

public interface MoveStrategy {
    List<Position> calculateMoves(ChessGameState chessGameState, ChessPiece chessPiece, GameUtils utils);
    default boolean isValidMove(Position position, GameUtils utils){
        return utils.isValidPosition(position);
    }
}
