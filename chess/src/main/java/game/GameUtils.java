package game;

import game.core.Color;
import game.object.ChessGameState;
import game.factory.ChessPiece;

import java.util.Optional;

public class GameUtils {
    private static final int BOARD_SIZE = 8;
    public GameUtils() {
    }

    public boolean isValidPosition(Position position) {
        int x = position.x();
        int y = position.y();
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    public boolean isPositionEmpty(Position position, ChessGameState chessGameState) {
        return chessGameState.getChessPieces().stream()
                .noneMatch(piece -> piece.getPosition().equals(position));
    }

    public boolean isPositionOccupiedByOpponent(Position position, Color currentPlayerColor, ChessGameState chessGameState) {
        return chessGameState.getChessPieces().stream()
                .anyMatch(piece -> piece.getPosition().equals(position) && !piece.getColor().equals(currentPlayerColor));
    }


    public Optional<ChessPiece> findPieceAtPosition(int x, int y, ChessGameState chessGameState) {
        return chessGameState.getChessPieces().stream()
                .filter(piece -> piece.getPosition().x() == x && piece.getPosition().y() == y)
                .findFirst();
    }

}