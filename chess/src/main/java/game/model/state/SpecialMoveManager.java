package game.model.state;

import game.Position;
import game.util.Color;

public interface SpecialMoveManager {


    boolean isRookUnmovedForCastling(Color color, Position kingPosition);

    Position getEnPassantTarget();

    char[] getCastlingRights();

    void setCanCastle(boolean b);

    void clearBoard();

}
