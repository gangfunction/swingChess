package hello;

import hello.core.Player;
import hello.gameobject.ChessBoard;
import hello.gameobject.ChessPiece;

import java.util.Optional;

public class GameUtils {
    public GameUtils() {
    }

    public boolean isValidPosition(Position position) {
        int x = position.getX();
        int y = position.getY();
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public boolean isPositionEmpty(Position position, ChessBoard chessBoard) {
        return chessBoard.getChessPieces().stream()
                .noneMatch(piece -> piece.getPosition().equals(position));
    }

    public boolean isPositionOccupiedByOpponent(Position position, Player.Color currentPlayerColor, ChessBoard chessBoard) {
        return chessBoard.getChessPieces().stream()
                .anyMatch(piece -> piece.getPosition().equals(position) && piece.getColor() != currentPlayerColor);
    }

    public boolean isValidPosition(Position position, ChessBoard chessBoard) {
        return isValidPosition(position);
    }

    public Optional<ChessPiece> findPieceAtPosition(int x, int y, ChessBoard chessBoard) {
        return chessBoard.getChessPieces().stream()
                .filter(piece -> piece.getPosition().getX() == x && piece.getPosition().getY() == y)
                .findFirst();
    }
}