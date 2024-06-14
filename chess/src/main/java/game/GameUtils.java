package game;

import game.model.state.ChessPieceManager;
import game.util.Color;
import game.core.factory.ChessPiece;

import java.util.Map;
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

    public static boolean isPositionEmpty(Position position, ChessPieceManager chessPieceManager) {
        return chessPieceManager.getChessPieces().values().stream()
                .noneMatch(piece -> piece.getPosition().equals(position));
    }

    public static boolean isPositionOccupiedByOpponent(Position position, Color currentPlayerColor, ChessPieceManager chessPieceManager) {
        return chessPieceManager.getChessPieces().values().stream()
                .anyMatch(piece -> piece.getPosition().equals(position) && !piece.getColor().equals(currentPlayerColor));
    }

    public static Optional<ChessPiece> findPieceAtPosition(ChessPieceManager chessPiceManager, Position position) {
        return chessPiceManager.getChessPieces().values().stream()
                .filter(piece -> piece.getPosition().equals(position))
                .findFirst();
    }
}