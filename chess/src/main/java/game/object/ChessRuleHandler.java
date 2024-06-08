package game.object;

import game.Position;
import game.core.Color;
import game.factory.ChessPiece;
import game.factory.PieceType;

public class ChessRuleHandler {
    public boolean isKingInCheck(Color color, GameStatusListener gameStatusListener) {

        ChessPiece king = findKing(color, gameStatusListener);
        if (king == null) {
            throw new IllegalStateException("King not found for color " + color);
        }
        Position kingPosition = king.getPosition();
        return gameStatusListener.getChessPieces().values().stream()
                .filter(piece -> piece.getColor() != color)
                .flatMap(piece -> piece.calculateMoves(gameStatusListener).stream())
                .anyMatch(move -> move.equals(kingPosition));
    }
    private ChessPiece findKing(Color color, GameStatusListener gameStatusListener) {
        return gameStatusListener.getChessPieces().values().stream()
                .filter(piece -> piece.getType() == PieceType.KING && piece.getColor() == color)
                .findFirst()
                .orElse(null);
    }

    boolean checkEnPassantCondition(ChessPiece selectedPiece, Position moveTo, GameStatusListener gameStatusListener) {
        if (selectedPiece.getType() != PieceType.PAWN) return false;
        int direction = selectedPiece.getColor() == Color.WHITE ? 1 : -1;
        Position adjacentPawnPosition = new Position(moveTo.x(), moveTo.y() + direction);
        ChessPiece adjacentPawn = gameStatusListener.getChessPieceAt(adjacentPawnPosition);
        if (adjacentPawn ==null) return false;
        return adjacentPawn.getType() == PieceType.PAWN;
    }
}
