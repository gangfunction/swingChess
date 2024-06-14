package game.util;

public enum PieceType {
    PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING;

    public static PieceType fromFenSymbol(char c) {
        return switch (c) {
            case 'P', 'p' -> PAWN;
            case 'R', 'r' -> ROOK;
            case 'N', 'n' -> KNIGHT;
            case 'B', 'b' -> BISHOP;
            case 'Q', 'q' -> QUEEN;
            case 'K', 'k' -> KING;
            default -> throw new IllegalArgumentException("Invalid FEN symbol: " + c);
        };
    }

    public char[] getFenSymbol(Color color) {
        return switch (this) {
            case PAWN -> color == Color.WHITE ? new char[]{'P'} : new char[]{'p'};
            case ROOK -> color == Color.WHITE ? new char[]{'R'} : new char[]{'r'};
            case KNIGHT -> color == Color.WHITE ? new char[]{'N'} : new char[]{'n'};
            case BISHOP -> color == Color.WHITE ? new char[]{'B'} : new char[]{'b'};
            case QUEEN -> color == Color.WHITE ? new char[]{'Q'} : new char[]{'q'};
            case KING -> color == Color.WHITE ? new char[]{'K'} : new char[]{'k'};
        };
    }
}
