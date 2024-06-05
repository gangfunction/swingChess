package game.status;

import game.GameUtils;
import game.Position;
import game.core.Color;
import game.core.GameTurnListener;
import game.factory.ChessPiece;
import game.object.GameStatusListener;
import game.strategy.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VictoryCondition {

    private GameStatusListener chessGameState;
    private GameUtils gameUtils;
    private GameTurnListener chessGameTurn;

    public void setVictoryCondition(GameStatusListener chessGameState, GameUtils gameUtils, GameTurnListener chessGameTurn) {
        this.chessGameState = chessGameState;
        this.gameUtils = gameUtils;
        this.chessGameTurn = chessGameTurn;
    }

    public boolean isCheckMate() {
        ChessPiece king = chessGameState.getKing(chessGameTurn.getCurrentPlayerColor());
        if (king == null) {
            throw new IllegalStateException("King not found for current player");
        }
        return isKingInCheck(king) && !canMoveToSafety(king) && !canOtherPiecesProtectKing(king);
    }

    private boolean canMoveToSafety(ChessPiece king) {
        return new KingStrategy().calculateMoves(chessGameState, king, gameUtils).stream()
                .noneMatch(move -> isPositionUnderThreat(move, king.getColor()));
    }

    private boolean canOtherPiecesProtectKing(ChessPiece king) {
        return chessGameState.getChessPieces().stream()
                .filter(piece -> piece.getColor() == king.getColor())
                .flatMap(piece -> calculateMovesForPiece(piece).stream())
                .noneMatch(move -> isPositionUnderThreat(move, king.getColor()));
    }

    public boolean isKingInCheck(ChessPiece king) {
        return isPositionUnderThreat(king.getPosition(), king.getColor());
    }

    public boolean isPositionUnderThreat(Position position, Color color) {
        return chessGameState.getChessPieces().stream()
                .filter(piece -> piece.getColor() != color)
                .flatMap(piece -> calculateMovesForPiece(piece).stream())
                .anyMatch(move -> move.equals(position));
    }

    private Set<Position> calculateMovesForPiece(ChessPiece piece) {
        MoveStrategy strategy = getMoveStrategy(piece);
        return new HashSet<>(strategy.calculateMoves(chessGameState, piece, gameUtils));
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
}