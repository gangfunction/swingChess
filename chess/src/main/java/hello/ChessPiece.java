package hello;


public class ChessPiece {
    private Type type;
    private Position position;
    private Player.Color color;

    // 체스말의 종류를 나타내는 열거형
    public enum Type {
        PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
    }

    public ChessPiece(Type type, Position position, Player.Color color) {
        this.type = type;
        this.position = position;
        this.color = color;
    }

    // 체스말의 위치를 설정하는 메서드
    public void setPosition(Position position) {
        this.position = position;
    }

    // 체스말의 현재 위치를 가져오는 메서드
    public Position getPosition() {
        return position;
    }

    // 체스말의 색상을 가져오는 메서드
    public Player.Color getColor() {
        return color;
    }

    // 체스말의 종류를 가져오는 메서드
    public Type getType() {
        return type;
    }
}