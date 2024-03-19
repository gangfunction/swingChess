package game;

import game.factory.ChessPiece;

import java.util.List;

public interface GameEventListener {
    void onPieceMoved( Position newPosition, ChessPiece piece);
    void onPieceSelected(ChessPiece piece);
    void onInvalidMoveAttempted(String reason);
    void onMovesCalculated(List<Position> moves);
}
