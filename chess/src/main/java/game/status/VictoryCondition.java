package game.status;

import game.Position;
import game.core.Color;
import game.core.GameTurnListener;
import game.factory.ChessPiece;
import game.object.GameStatusListener;
import game.strategy.*;

import java.util.HashSet;
import java.util.Set;

public class VictoryCondition {

    private GameStatusListener chessGameState;
    private GameTurnListener chessGameTurn;
    private Boolean isKingInCheckCache = null;

    public void setVictoryCondition(GameStatusListener chessGameState,GameTurnListener chessGameTurn) {
        if (chessGameState == null ||  chessGameTurn == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        this.chessGameState = chessGameState;
        this.chessGameTurn = chessGameTurn;
    }

    private void ensureInitialized() {
        if (chessGameState == null || chessGameTurn == null) {
            throw new IllegalStateException("VictoryCondition not initialized");
        }
    }

    public boolean isCheckMate() {
        ensureInitialized();
        ChessPiece king = chessGameState.getKing(chessGameTurn.getCurrentPlayerColor());
        if (king == null) {
            throw new IllegalStateException("King not found for current player");
        }
        return isKingInCheck(king) && !canMoveToSafety(king) && !canOtherPiecesProtectKing(king);
    }


    private boolean canMoveToSafety(ChessPiece king) {
        return new KingStrategy().calculateMoves(chessGameState, king).stream()
                .anyMatch(move -> !isPositionUnderThreat(move, king.getColor()));
    }


    private boolean canOtherPiecesProtectKing(ChessPiece king) {
        return chessGameState.getChessPieces().values().stream()
                .filter(piece -> piece.getColor() == king.getColor() && !piece.equals(king))
                .anyMatch(piece -> {
                    Set<Position> moves = calculateMovesForPiece(piece);
                    Position originalPosition = piece.getPosition();
                    for (Position move : moves) {
                        piece.setPosition(move);
                        boolean isSafe = !isKingInCheck(king);
                        piece.setPosition(originalPosition);
                        if (isSafe) {
                            return true;
                        }
                    }
                    return false;
                });
    }

    public boolean isKingInCheck(ChessPiece king) {
        ensureInitialized();
        if (isKingInCheckCache != null) {
            return isKingInCheckCache;
        }
        boolean inCheck = isPositionUnderThreat(king.getPosition(), king.getColor());
        isKingInCheckCache = inCheck;
        return inCheck;
    }

    public boolean isPositionUnderThreat(Position position, Color color) {
        ensureInitialized();
        return chessGameState.getChessPieces().values().parallelStream()
                .filter(piece -> piece.getColor() != color)
                .flatMap(piece -> calculateMovesForPiece(piece).stream())
                .anyMatch(move -> move.equals(position));
    }

    private Set<Position> calculateMovesForPiece(ChessPiece piece) {
        MoveStrategy strategy = getMoveStrategy(piece);
        return new HashSet<>(strategy.calculateMoves(chessGameState, piece));
    }

    private MoveStrategy getMoveStrategy(ChessPiece piece) {
        return switch (piece.getType()) {
            case PAWN -> new PawnStrategy();
            case KNIGHT -> new KnightStrategy();
            case BISHOP -> new BishopStrategy();
            case ROOK -> new RookStrategy();
            case QUEEN -> new QueenStrategy();
            case KING -> new KingStrategy();
        };
    }

    public void invalidateKingInCheckCache() {
        isKingInCheckCache = null;
    }
}