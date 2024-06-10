package game.model;

import game.Position;
import game.util.Color;
import game.core.factory.ChessPiece;

import java.util.Map;
import java.util.Stack;

public interface GameStatusListener {
    void addChessPiece(Position move, ChessPiece chessPiece);

    Stack<ChessPiece> getCapturedPieces();

    Map<Position, ChessPiece> getChessPieces();

    ChessPiece getSelectedPiece();

    void setSelectedPiece(ChessPiece piece);

    void updateLastMovedPawn(ChessPiece pawn, Position from, Position to);

    ChessPiece getChessPieceAt(Position targetPosition);

    ChessPiece getLastMovedPiece();

    boolean getLastMoveWasDoubleStep();

    void removeChessPiece(ChessPiece targetPawn);

    boolean isRookUnmovedForCastling(Color color, Position kingPosition);

    void updateMoveWithoutPawnOrCaptureCount(boolean isPawnMove, boolean isCapture);

    int getMoveWithoutPawnOrCaptureCount();

    boolean isAvailableMoveTarget(Position position, GameLogicActions gameLogicActions);

    ChessPiece getKing(Color color);

    Position getEnPassantTarget();

    char[] getCastlingRights();

    void setCanCastle(boolean b);

    void clearBoard();

}
