package game.util;

import game.strategy.*;

public class MoveStrategyFactory {
    public static MoveStrategy createMoveStrategy(PieceType pieceType) {
        return switch (pieceType) {
            case PAWN -> new PawnStrategy();
            case ROOK -> new RookStrategy();
            case KNIGHT -> new KnightStrategy();
            case BISHOP -> new BishopStrategy();
            case QUEEN -> new QueenStrategy();
            case KING -> new KingStrategy();
        };
    }
}
