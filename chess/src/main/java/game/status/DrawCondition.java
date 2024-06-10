package game.status;

import game.Position;
import game.util.Color;
import game.core.GameTurnListener;
import game.core.factory.ChessPiece;
import game.util.PieceType;
import game.model.GameLogicActions;
import game.model.GameStatusListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DrawCondition {
    private GameStatusListener gameStatusListener;
    private GameLogicActions gameLogicActions;
    private GameTurnListener gameTurnListener;
    private final Map<String, Integer> gameStateOccurrences = new HashMap<>();

    public void setDrawCondition(GameStatusListener chessGameState, GameLogicActions chessGameLogic, GameTurnListener chessGameTurn) {
        this.gameStatusListener = chessGameState;
        this.gameLogicActions = chessGameLogic;
        this.gameTurnListener = chessGameTurn;
    }

    public boolean isDraw() {
        return isInsufficientMaterial() || isThreefoldRepetition() || isFiftyMoveRule();
    }

    private boolean isThreefoldRepetition() {
        String currentState = gameTurnListener.serializeGameState();
        gameStateOccurrences.put(currentState, gameStateOccurrences.getOrDefault(currentState, 0) + 1);
        return gameStateOccurrences.get(currentState) >= 3;
    }

    public boolean isStalemate(Color currentPlayerColor) {
        Map<Position, ChessPiece> currentPlayerPieces = getCurrentPlayerPieces(currentPlayerColor);

        for (ChessPiece piece : currentPlayerPieces.values()) {
            if (hasLegalMove(piece, currentPlayerColor)) {
                return false;
            }
        }
        return true;
    }

    private Map<Position, ChessPiece> getCurrentPlayerPieces(Color color) {
        return gameStatusListener.getChessPieces().values().stream()
                .filter(piece -> piece.getColor() == color)
                .collect(Collectors.toMap(
                        ChessPiece::getPosition,
                        Function.identity(),
                        (existing, replacement) -> existing // 중복 키가 발생할 경우 기존 값을 유지
                ));
    }

    private boolean hasLegalMove(ChessPiece piece, Color currentPlayerColor) {
        Set<Position> possibleMoves = gameLogicActions.calculateMovesForPiece(piece);
        for (Position move : possibleMoves) {
            if (tryMoveAndCheck(piece, move, currentPlayerColor)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryMoveAndCheck(ChessPiece piece, Position move, Color currentPlayerColor) {
        Position originalPosition = piece.getPosition();
        ChessPiece capturedPiece = gameStatusListener.getChessPieceAt(move);

        movePiece(piece, move, capturedPiece);
        boolean isInCheck = gameLogicActions.isKingInCheck(currentPlayerColor);
        undoMove(piece, originalPosition, capturedPiece);

        return !isInCheck;
    }

    private void movePiece(ChessPiece piece, Position move, ChessPiece capturedPiece) {
        if (capturedPiece != null) {
            gameStatusListener.removeChessPiece(capturedPiece);
        }
        piece.setPosition(move);
        piece.setMoved(true);
    }

    private void undoMove(ChessPiece piece, Position originalPosition, ChessPiece capturedPiece) {
        piece.setPosition(originalPosition);
        piece.setMoved(false);
        if (capturedPiece != null) {
            gameStatusListener.addChessPiece(originalPosition, capturedPiece);
        }
    }

    private boolean isInsufficientMaterial() {
        Map<Position, ChessPiece> pieces = gameStatusListener.getChessPieces();
        long nonKingCount = pieces.values().stream().filter(piece -> piece.getType() != PieceType.KING).count();
        if (nonKingCount == 0) {
            return true;
        } else if (nonKingCount == 1) {
            return hasSingleMinorPiece(pieces);
        } else {
            return hasOnlyBishopsOfSameColor(pieces);
        }
    }
    private boolean hasSingleMinorPiece(Map<Position, ChessPiece> pieces) {
        long bishopCount = pieces.values().stream().filter(piece -> piece.getType() == PieceType.BISHOP).count();
        return bishopCount == 1 || pieces.values().stream().anyMatch(p -> p.getType() == PieceType.KNIGHT);
    }

    private boolean hasOnlyBishopsOfSameColor(Map<Position, ChessPiece> pieces) {
        long bishopCount = pieces.values().stream().filter(piece -> piece.getType() == PieceType.BISHOP).count();
        return bishopCount == pieces.size() - 1 && isBishopColorConsistent(pieces);
    }

    private boolean isBishopColorConsistent(Map<Position, ChessPiece> pieces) {
        Integer bishopColor = null;
        for (ChessPiece piece : pieces.values()) {
            if (piece.getType() == PieceType.BISHOP) {
                int color = (piece.getPosition().x() + piece.getPosition().y()) % 2;
                if (bishopColor == null) {
                    bishopColor = color;
                } else if (!bishopColor.equals(color)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isFiftyMoveRule() {
        int moveWithoutPawnOrCapture = gameStatusListener.getMoveWithoutPawnOrCaptureCount();
        return moveWithoutPawnOrCapture >= 50;
    }
}