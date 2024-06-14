package game.model.state;

import game.Position;
import game.core.factory.ChessPiece;
import game.util.Color;

public interface SpecialMoveManager {

    boolean isRookUnmovedForCastling(Color color, Position kingPosition);

    void setCanCastle(boolean b);

    void clearBoard();

    int getHalfMoveClock();

    boolean isKingSideCastlingAllowed(Color color);

    boolean isQueenSideCastlingAllowed(Color color);

    ChessPiece getLastMovedPiece();

    boolean getLastMoveWasDoubleStep();

    void setCastlingRights(String part);

    void setEnPassantTarget(Position position);

    void setHalfMoveClock(int i);
}
