package hello;

import hello.gameobject.ChessBoardUI;
import hello.gameobject.ChessPiece;

public interface GameEventListener {
    void onPieceMoved(Position oldPosition, Position newPosition, ChessPiece piece);
    void onPieceSelected(ChessPiece piece);
    void onInvalidMoveAttempted();
}
