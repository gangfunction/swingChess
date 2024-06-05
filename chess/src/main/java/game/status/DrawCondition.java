package game.status;

import game.Position;
import game.core.Color;
import game.core.GameTurnListener;
import game.factory.ChessPiece;
import game.factory.PieceType;
import game.object.GameLogicActions;
import game.object.GameStatusListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public boolean isDraw(Color currentPlayerColor) {
        return isStalemate(currentPlayerColor) || isInsufficientMaterial() || isThreefoldRepetition() || isFiftyMoveRule();
    }

    private boolean isThreefoldRepetition() {
        String currentState = gameTurnListener.serializeGameState();
        gameStateOccurrences.put(currentState, gameStateOccurrences.getOrDefault(currentState, 0) + 1);
        return gameStateOccurrences.get(currentState) >= 3;
    }

    public boolean isStalemate(Color currentPlayerColor) {
        List<ChessPiece> currentPlayerPieces = getCurrentPlayerPieces(currentPlayerColor);

        for (ChessPiece piece : currentPlayerPieces) {
            if (hasLegalMove(piece, currentPlayerColor)) {
                return false;
            }
        }
        return true;
    }

    private List<ChessPiece> getCurrentPlayerPieces(Color currentPlayerColor) {
        return gameStatusListener.getChessPieces().stream()
                .filter(piece -> piece.getColor() == currentPlayerColor)
                .collect(Collectors.toList());
    }

    private boolean hasLegalMove(ChessPiece piece, Color currentPlayerColor) {
        List<Position> possibleMoves = gameLogicActions.calculateMovesForPiece(piece);
        for (Position move : possibleMoves) {
            if (tryMoveAndCheck(piece, move, currentPlayerColor)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryMoveAndCheck(ChessPiece piece, Position move, Color currentPlayerColor) {
        Position originalPosition = piece.getPosition();
        ChessPiece capturedPiece = gameStatusListener.getChessPieceAt(move).orElse(null);

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
            gameStatusListener.addChessPiece(capturedPiece);
        }
    }

    private boolean isInsufficientMaterial() {
        List<ChessPiece> pieces = gameStatusListener.getChessPieces();
        long nonKingCount = pieces.stream().filter(piece -> piece.getType() != PieceType.KING).count();
        long bishopCount = pieces.stream().filter(piece -> piece.getType() == PieceType.BISHOP).count();
        boolean isBishopColorConsistent = isBishopColorConsistent(pieces);

        if (nonKingCount == 0) {
            return true;
        } else if (nonKingCount == 1) {
            return bishopCount == 1 || pieces.stream().anyMatch(p -> p.getType() == PieceType.KNIGHT);
        } else {
            return bishopCount == nonKingCount && isBishopColorConsistent;
        }
    }

    private boolean isBishopColorConsistent(List<ChessPiece> pieces) {
        Integer bishopColor = null;
        for (ChessPiece piece : pieces) {
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