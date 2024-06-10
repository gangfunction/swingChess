package game.ui;

import game.Position;
import game.core.factory.ChessPiece;

import javax.swing.*;
import java.util.Set;

public interface GameEventListener {
    void addPieceToPanel(JPanel panel, JLabel pieceLabel);

    void onPieceMoved(Position newPosition, ChessPiece piece);

    void onInvalidMoveAttempted(String reason);

    void highlightPossibleMoves(ChessPiece piece);

    void clearHighlights();

    void highlightMoves(Set<Position> moves);

    JPanel getPanelAtPosition(Position position);

    void placePieceOnboard(Position move, ChessPiece chessPiece);

    void onPieceRemoved(ChessPiece piece);
}
