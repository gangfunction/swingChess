package game.status;

import game.Position;
import game.model.state.ChessPieceManager;
import game.model.state.MoveManager;
import game.util.Color;
import game.core.GameTurnListener;
import game.core.PlayerManager;
import game.core.factory.ChessPiece;
import game.model.state.SpecialMoveManager;
import game.strategy.*;

import java.util.HashSet;
import java.util.Set;

public class VictoryCondition {

    private SpecialMoveManager chessGameState;
    private GameTurnListener chessGameTurn;
    private Boolean isKingInCheckCache = null;
    private ChessPieceManager chessPieceManager;
    private MoveManager moveManager;


    public void setVictoryCondition(SpecialMoveManager chessGameState,
                                    GameTurnListener chessGameTurn,
                                    ChessPieceManager chessPieceManager,
                                    MoveManager moveManager
    ) {
        if (chessGameState == null ||  chessGameTurn == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        this.chessGameState = chessGameState;
        this.chessGameTurn = chessGameTurn;
        this.chessPieceManager = chessPieceManager;
        this.moveManager = moveManager;
    }

    private void ensureInitialized() {
        if (chessGameState == null || chessGameTurn == null) {
            throw new IllegalStateException("VictoryCondition not initialized");
        }
    }

    public boolean isCheckMate(PlayerManager playerManager) {
        ensureInitialized();
        ChessPiece king = chessPieceManager.getKing(playerManager.getCurrentPlayerColor());
        if (king == null) {
            throw new IllegalStateException("King not found for current player");
        }
        return isKingInCheck(king) && !canMoveToSafety(king) && !canOtherPiecesProtectKing(king);
    }


    private boolean canMoveToSafety(ChessPiece king) {
        return new KingStrategy().calculateMoves(chessPieceManager,moveManager, king).stream()
                .anyMatch(move -> !isPositionUnderThreat(move, king.getColor()));
    }


    private boolean canOtherPiecesProtectKing(ChessPiece king) {
        return chessPieceManager.getChessPieces().values().stream()
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
        return chessPieceManager.getChessPieces().values().parallelStream()
                .filter(piece -> piece.getColor() != color)
                .flatMap(piece -> calculateMovesForPiece(piece).stream())
                .anyMatch(move -> move.equals(position));
    }

    private Set<Position> calculateMovesForPiece(ChessPiece piece) {
        MoveStrategy strategy = piece.getMoveStrategy();
        return new HashSet<>(strategy.calculateMoves(chessPieceManager,moveManager, piece));
    }

    public void invalidateKingInCheckCache() {
        isKingInCheckCache = null;
    }
}