package game.model;

import game.Position;
import game.core.factory.ChessPiece;

public interface MoveManager {
    void updateLastMovedPawn(ChessPiece pawn, Position from, Position to);
    ChessPiece getLastMovedPiece();
    boolean getLastMoveWasDoubleStep();
    void updateMoveWithoutPawnOrCaptureCount(boolean isPawnMove, boolean isCapture);
    int getMoveWithoutPawnOrCaptureCount();
    boolean isAvailableMoveTarget(Position position, GameLogicActions gameLogicActions);
}