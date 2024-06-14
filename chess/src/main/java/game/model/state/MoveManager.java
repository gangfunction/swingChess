package game.model.state;

import game.Position;
import game.core.factory.ChessPiece;
import game.model.GameLogicActions;

public interface MoveManager {

    void updateLastMovedPawn(ChessPiece pawn, Position from, Position to);

    ChessPiece getLastMovedPiece();

    boolean getLastMoveWasDoubleStep();

    void updateMoveWithoutPawnOrCaptureCount(boolean isPawnMove, boolean isCapture);

    boolean isAvailableMoveTarget(Position position, GameLogicActions gameLogicActions);

    Position getEnPassantTarget();
}