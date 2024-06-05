package game.object;

import game.Position;
import game.factory.ChessPiece;

import javax.swing.*;
import java.util.List;

public interface GameEventListener {
    void addPieceToPanel(JPanel panel, JLabel pieceLabel);

    void onPieceMoved(Position newPosition, ChessPiece piece);
    void onInvalidMoveAttempted(String reason);

    void highlightPossibleMoves(ChessPiece piece);
    void clearHighlights();
    void highlightMoves(List<Position> moves);
    JPanel getPanelAtPosition(Position position);
    void onGameDraw();
}
