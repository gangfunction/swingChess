package game.core;

import game.util.Color;
import lombok.Getter;

import java.util.Objects;

/**
 * @param name  플레이어의 이름
 * @param color 플레이어가 가진 체스말의 색상
 */
@Getter
public record Player(String name, Color color) {
    public Player {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", color=" + color +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return color == player.color && Objects.equals(name, player.name);
    }
}