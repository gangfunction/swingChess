package game.model.state;

import game.Position;
import game.model.*;
import game.util.Color;
import game.core.factory.ChessPiece;
import game.util.PieceType;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChessGameState implements SpecialMoveManager, CapturedPieceManager, ChessPieceManager, MoveManager {
    private Position enPassantTarget = null;
    private final Map<Position, ChessPiece> chessPieces = new ConcurrentHashMap<>();
    @Getter
    private final Stack<ChessPiece> capturedPieces = new Stack<>();
    private ChessPiece selectedPiece = null;
    private ChessPiece lastMovedPiece = null;
    private boolean lastMoveWasDoubleStep = false;
    private boolean canCastle = false;
    private int moveWithoutPawnOrCaptureCount = 0;
    private boolean isQueenSideCastlingAllowed = false;
    private boolean isKingSideCastlingAllowed = false;

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
    public void setCastlingRights(String part) {
        isKingSideCastlingAllowed = false;
        isQueenSideCastlingAllowed = false;

        for (char c : part.toCharArray()) {
            switch (c) {
                case 'K', 'k' -> isKingSideCastlingAllowed = true;
                case 'Q', 'q' -> isQueenSideCastlingAllowed = true;
            }
        }
    }
    @Override
    public void setEnPassantTarget(Position position) {
        if (position == null) {
            enPassantTarget = null;
            return;
        }

        ChessPiece lastMovedPawn = getLastMovedPiece();
        if (lastMovedPawn == null || !lastMovedPawn.getType().equals(PieceType.PAWN)) {
            enPassantTarget = null;
            return;
        }

        boolean lastMoveWasDoubleStep = getLastMoveWasDoubleStep();
        if (!lastMoveWasDoubleStep) {
            enPassantTarget = null;
            return;
        }

        Position lastMovedPosition = lastMovedPawn.getPosition();
        Color color = lastMovedPawn.getColor();
        int direction = getMoveDirection(color);

        // 앙파상 타겟 위치 계산
        int targetX = lastMovedPosition.x();
        int targetY = lastMovedPosition.y() + (-direction);

        if (targetX == position.x() && targetY == position.y()) {
            enPassantTarget = position;
        } else {
            enPassantTarget = null;
        }
    }

    private int getMoveDirection(Color color) {
        return color == Color.WHITE ? -1 : 1;
    }
    @Override
    public void setHalfMoveClock(int halfMoveClock) {
        this.moveWithoutPawnOrCaptureCount = halfMoveClock;
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
    public int getHalfMoveClock() {
        return moveWithoutPawnOrCaptureCount;
    }

    @Override
    public boolean isKingSideCastlingAllowed(Color color) {
        return isKingSideCastlingAllowed;
    }

    @Override
    public boolean isQueenSideCastlingAllowed(Color color) {
        return isQueenSideCastlingAllowed;
    }

    @Override
    public boolean isAvailableMoveTarget(Position position, GameLogicActions gameLogicActions) {
        ChessPiece thisPiece = getSelectedPiece();
        if (thisPiece == null) {
            return false;
        }
        if(canCastle && position.equals(new Position(2, thisPiece.getPosition().y()))){
            gameLogicActions.setAfterCastling(true);
            isQueenSideCastlingAllowed = true;

            return true;
        }
        if (canCastle && position.equals(new Position(6, thisPiece.getPosition().y()))) {
            gameLogicActions.setAfterCastling(true);
            isKingSideCastlingAllowed = true;
            return true;
        }
        Set<Position> validMoves = gameLogicActions.calculateMovesForPiece(thisPiece);
        return validMoves.contains(position) && !gameLogicActions.isFriendlyPieceAtPosition(position, thisPiece);
    }

    @Override
    public Position getEnPassantTarget() {
        return enPassantTarget;
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
    public char[] getCastlingRights() {
        StringBuilder rights = new StringBuilder();

        if (isKingSideCastlingAllowed) {
            rights.append('K');
        }
        if (isQueenSideCastlingAllowed) {
            rights.append('Q');
        }

        return rights.toString().toCharArray();
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