package game.util;

public enum Color {
    WHITE, BLACK;

    public boolean isComputer() {
        return this == BLACK;
    }

    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}
