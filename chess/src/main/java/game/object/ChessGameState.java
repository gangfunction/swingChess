package game.object;

import game.Position;
import game.core.Color;
import game.factory.ChessPiece;
import game.factory.PieceType;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChessGameState implements GameStatusListener {

    private final List<ChessPiece> chessPieces = new ArrayList<>();
    private ChessPiece selectedPiece = null;
    private ChessPiece lastMovedPiece = null;
    private boolean lastMoveWasDoubleStep = false;
    private boolean canCastle = false;
    @Setter
    private GameLogicActions gameLogicActions;
    private int moveWithoutPawnOrCaptureCount = 0;

    @Override
    public void addChessPiece(ChessPiece chessPiece) {
        chessPieces.add(chessPiece);
    }

    @Override
    public List<ChessPiece> getChessPieces() {
        return new ArrayList<>(chessPieces);
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
            resetLastMove();
        }
    }

    private void resetLastMove() {
        this.lastMovedPiece = null;
        this.lastMoveWasDoubleStep = false;
    }

    @Override
    public Optional<ChessPiece> getChessPieceAt(Position targetPosition) {
        return chessPieces.stream()
                .filter(piece -> piece.getPosition().equals(targetPosition))
                .findFirst();
    }

    @Override
    public List<ChessPiece> getChessPiecesAt(Position targetPosition) {
        List<ChessPiece> pieces = new ArrayList<>();
        for (ChessPiece piece : chessPieces) {
            if (piece.getPosition().equals(targetPosition)) {
                pieces.add(piece);
            }
        }
        return pieces;
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
        chessPieces.remove(targetPawn);
    }

    @Override
    public boolean isRookUnmovedForCastling(Color color, Position kingPosition) {
        Position rookPosition = kingPosition.x() == 2 ?
                new Position(0, kingPosition.y()) : // 퀸 사이드 캐슬링
                new Position(7, kingPosition.y());  // 킹 사이드 캐슬링

        return getChessPieceAt(rookPosition)
                .filter(rook -> rook.getType() == PieceType.ROOK && rook.getColor() == color && rook.isMoved())
                .isPresent();
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
    public boolean isAvailableMoveTarget(Position position, ChessGameLogic chessGameLogic) {
        ChessPiece thisPiece = getSelectedPiece();
        if (thisPiece == null) {
            return false;
        }
        List<Position> validMoves = chessGameLogic.calculateMovesForPiece(thisPiece);
        if (canCastle) {
            validMoves.add(new Position(2, thisPiece.getPosition().y()));
            validMoves.add(new Position(6, thisPiece.getPosition().y()));
            gameLogicActions.setAfterCastling(true);
        }

        return validMoves.contains(position) && !chessGameLogic.isFriendlyPieceAtPosition(position, thisPiece);
    }

    @Override
    public ChessPiece getKing(Color color) {
        return chessPieces.stream()
                .filter(piece -> piece.getType() == PieceType.KING && piece.getColor() == color)
                .findFirst()
                .orElse(null);
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


}