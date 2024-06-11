package game.model.state;

import game.Position;
import game.model.*;
import game.util.Color;
import game.core.factory.ChessPiece;
import game.util.PieceType;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChessGameState implements SpecialMoveManager, CapturedPieceManager, ChessPieceManager, MoveManager {

    private final Map<Position, ChessPiece> chessPieces = new ConcurrentHashMap<>();
    @Getter
    private final Stack<ChessPiece> capturedPieces = new Stack<>();
    private ChessPiece selectedPiece = null;
    private ChessPiece lastMovedPiece = null;
    private boolean lastMoveWasDoubleStep = false;
    private boolean canCastle = false;
    @Setter
    private GameLogicActions gameLogicActions;
    private int moveWithoutPawnOrCaptureCount = 0;

    @Override
    public void addChessPiece(Position move, ChessPiece chessPiece) {
        chessPieces.put(move, chessPiece);
    }

    @Override
    public Map<Position, ChessPiece> getChessPieces() {
        return chessPieces;
    }

    @Override
    public ChessPiece getSelectedPiece() {
        return selectedPiece;
    }

    @Override
    public void setSelectedPiece(ChessPiece piece) {
        this.selectedPiece = piece;
    }

    @Override
    public void updateLastMovedPawn(ChessPiece pawn, Position from, Position to) {
        int distanceMoved = Math.abs(from.y() - to.y());
        if (distanceMoved == 2) {
            this.lastMovedPiece = pawn;
            this.lastMoveWasDoubleStep = true;
        } else {
            this.lastMovedPiece = null;
            this.lastMoveWasDoubleStep = false;
        }
    }

    @Override
    public ChessPiece getChessPieceAt(Position targetPosition) {
        return chessPieces.get(targetPosition);
    }

    @Override
    public ChessPiece getLastMovedPiece() {
        return this.lastMovedPiece;
    }

    @Override
    public boolean getLastMoveWasDoubleStep() {
        return this.lastMoveWasDoubleStep;
    }

    @Override
    public void removeChessPiece(ChessPiece targetPawn) {
        chessPieces.remove(targetPawn.getPosition());
        capturedPieces.push(targetPawn);
    }

    @Override
    public boolean isRookUnmovedForCastling(Color color, Position kingPosition) {
        int rookX = (kingPosition.x() == 2) ? 0 : 7;
        Position rookPosition = new Position(rookX, kingPosition.y());
        ChessPiece rook = getChessPieceAt(rookPosition);
        return rook != null && rook.getType() == PieceType.ROOK && rook.getColor() == color && !rook.isMoved();
    }

    @Override
    public void updateMoveWithoutPawnOrCaptureCount(boolean isPawnMove, boolean isCapture) {
        if (isPawnMove || isCapture) {
            moveWithoutPawnOrCaptureCount = 0;
        } else {
            moveWithoutPawnOrCaptureCount++;
        }
    }

    @Override
    public int getMoveWithoutPawnOrCaptureCount() {
        return moveWithoutPawnOrCaptureCount;
    }

    @Override
    public boolean isAvailableMoveTarget(Position position, GameLogicActions gameLogicActions) {
        ChessPiece thisPiece = getSelectedPiece();
        if (thisPiece == null) {
            return false;
        }
        if (canCastle && (position.equals(new Position(2, thisPiece.getPosition().y())) ||
                position.equals(new Position(6, thisPiece.getPosition().y())))) {
            gameLogicActions.setAfterCastling(true);
            return true;
        }
        Set<Position> validMoves = gameLogicActions.calculateMovesForPiece(thisPiece);
        return validMoves.contains(position) && !gameLogicActions.isFriendlyPieceAtPosition(position, thisPiece);
    }

    private final Map<Color, ChessPiece> kingCache = new ConcurrentHashMap<>();

    @Override
    public ChessPiece getKing(Color color) {
        return kingCache.computeIfAbsent(color, c -> getChessPieces().values().stream()
                .filter(piece -> piece.getType() == PieceType.KING && piece.getColor() == c)
                .findFirst()
                .orElse(null));
    }

    @Override
    public Position getEnPassantTarget() {
        return null;
    }

    @Override
    public char[] getCastlingRights() {
        return new char[0];
    }


    @Override
    public void setCanCastle(boolean canCastle) {
        this.canCastle = canCastle;
    }


    @Override
    public void clearBoard() {
        chessPieces.clear();
    }


}