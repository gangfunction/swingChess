package game;

public record Position(int x, int y) {
    public int toBoardIndex() {
        return (this.y * 8 + this.x);
    }

    public Position add(int i, int i1) {
        return new Position(this.x + i, this.y + i1);
    }
}
