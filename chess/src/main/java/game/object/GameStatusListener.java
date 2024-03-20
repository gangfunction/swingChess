package game.object;

import game.Position;
import game.core.Color;
import game.factory.ChessPiece;

import java.util.List;
import java.util.Optional;

public interface GameStatusListener {
    void addChessPiece(ChessPiece chessPiece);

    List<ChessPiece> getChessPieces();

    void clearChessPieces();

    ChessPiece getSelectedPiece();

    void setSelectedPiece(ChessPiece piece);

    // 필요에 따라 추가 메서드 구현
    void updateLastMovedPawn(ChessPiece pawn, Position from, Position to);

    Optional<ChessPiece> getChessPieceAt(Position targetPosition);

    ChessPiece getLastMovedPiece();

    boolean getLastMoveWasDoubleStep();

    void removeChessPiece(ChessPiece targetPawn);

    boolean isRookUnmovedForCastling(Color color, Position kingPosition);

    void updateMoveWithoutPawnOrCaptureCount(boolean isPawnMove, boolean isCapture);

    int getMoveWithoutPawnOrCaptureCount();

    boolean isAvailableMoveTarget(Position position, ChessGameLogic chessGameLogic);
}
