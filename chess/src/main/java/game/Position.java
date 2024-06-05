package game;

public record Position(int x, int y) {

    public Position add(int i, int i1) {
        return new Position(this.x + i, this.y + i1);
    }
}
