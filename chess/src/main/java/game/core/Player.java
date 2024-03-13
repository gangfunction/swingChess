package game.core;

import game.GameLog;

public class Player {
    private String name; // 플레이어의 이름
    private final Color color; // 플레이어가 가진 체스말의 색상

    public Player(Color color) {
        this.color = color;
    }


    // 플레이어의 색상을 정의하는 열거형
    public enum Color {
        WHITE, BLACK
    }

    public Player(String name, Color color, GameLog gameLog) {
        this.name = name;
        this.color = color;
    }

    // 이름을 가져오는 메서드
    public String getName() {
        return name;
    }

    // 플레이어의 체스말 색상을 가져오는 메서드
    public Color getColor() {
        return color;
    }

    // 플레이어 정보를 문자열로 반환하는 메서드
    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", color=" + color +
                '}';
    }
}