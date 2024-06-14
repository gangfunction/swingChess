package game.model;

import game.Position;
import game.model.state.ChessPieceManager;
import game.model.state.MoveManager;
import game.util.Color;
import game.core.factory.ChessPiece;
import game.util.PieceType;

public class ChessRuleHandler {
    private final ChessPieceManager chessPieceManager;
    private final MoveManager moveManager;

    public ChessRuleHandler(ChessPieceManager chessPieceManager, MoveManager moveManager) {
        this.chessPieceManager = chessPieceManager;
        this.moveManager = moveManager;
    }

    public boolean isKingInCheck(Color color) {

        ChessPiece king = findKing(color);
        if (king == null) {
            throw new IllegalStateException("King not found for color " + color);
        }
        Position kingPosition = king.getPosition();
        return chessPieceManager.getChessPieces().values().stream()
                .filter(piece -> piece.getColor() != color)
                .flatMap(piece -> piece.calculateMoves(chessPieceManager,moveManager).stream())
                .anyMatch(move -> move.equals(kingPosition));
    }
    private ChessPiece findKing(Color color) {
        return chessPieceManager.getChessPieces().values().stream()
                .filter(piece -> piece.getType() == PieceType.KING && piece.getColor() == color)
                .findFirst()
                .orElse(null);
    }

    boolean checkEnPassantCondition(ChessPiece selectedPiece, Position moveTo) {
        if (selectedPiece.getType() != PieceType.PAWN) return false;
        int direction = selectedPiece.getColor() == Color.WHITE ? 1 : -1;
        Position adjacentPawnPosition = new Position(moveTo.x(), moveTo.y() + direction);
        ChessPiece adjacentPawn = chessPieceManager.getChessPieceAt(adjacentPawnPosition);
        if (adjacentPawn ==null) return false;
        return adjacentPawn.getType() == PieceType.PAWN;
    }
}
