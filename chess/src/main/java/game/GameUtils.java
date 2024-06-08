package game;

import game.core.Color;
import game.factory.ChessPiece;
import game.object.GameStatusListener;

import java.util.Optional;

public class GameUtils {
    private static final int BOARD_SIZE = 8;
    private GameUtils() {
        throw new UnsupportedOperationException("Utility class");
    }


    public static boolean isValidPosition(Position position) {
        int x = position.x();
        int y = position.y();
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    public static boolean isPositionEmpty(Position position, GameStatusListener gameStatusListener) {
        return gameStatusListener.getChessPieces().values().stream()
                .noneMatch(piece -> piece.getPosition().equals(position));
    }

    public static boolean isPositionOccupiedByOpponent(Position position, Color currentPlayerColor, GameStatusListener gameStatusListener) {
        return gameStatusListener.getChessPieces().values().stream()
                .anyMatch(piece -> piece.getPosition().equals(position) && !piece.getColor().equals(currentPlayerColor));
    }

    public static Optional<ChessPiece> findPieceAtPosition(GameStatusListener gameStatusListener, Position position) {
        return gameStatusListener.getChessPieces().values().stream()
                .filter(piece -> piece.getPosition().equals(position))
                .findFirst();
    }
}