package game.strategy;

import game.core.factory.ChessPiece;
import game.Position;
import game.model.state.ChessPieceManager;
import game.model.state.MoveManager;

import java.util.HashSet;
import java.util.Set;

public class KingStrategy implements MoveStrategy {
    @Override
    public Set<Position> calculateMoves(ChessPieceManager chessPieceManager, MoveManager moveManager, ChessPiece piece) {
        Set<Position> validMoves = new HashSet<>();
        int x = piece.getPosition().x();
        int y = piece.getPosition().y();

        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1},  {1, 0},  {1, 1}
        };

        for (int[] direction : directions) {
            int newX = x + direction[0];
            int newY = y + direction[1];
            Position newPosition = new Position(newX, newY);

            if(isValidMove(newPosition, piece, chessPieceManager)){
                validMoves.add(newPosition);
            }
        }

        return validMoves;
    }
}
