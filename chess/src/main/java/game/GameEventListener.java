package game;

import game.object.ChessPiece;

public interface GameEventListener {
    void onPieceMoved(Position oldPosition, Position newPosition, ChessPiece piece);
    void onPieceSelected(ChessPiece piece);
    void onInvalidMoveAttempted();
}
