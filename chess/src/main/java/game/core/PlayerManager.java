package game.core;

import game.util.Color;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
    private static List<Player> players = new ArrayList<>();
    @Getter
    @Setter
    private static int currentPlayerIndex;

    public PlayerManager() {
        players = initializePlayers();
        currentPlayerIndex = 0;
    }

    public static void setPlayers(List<Player> players) {
        PlayerManager.players.clear();
        PlayerManager.players = players;
    }

    private List<Player> initializePlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("pin", Color.WHITE));
        players.add(new Player("jake", Color.BLACK));
        return players;
    }

    public Player getCurrentPlayer() {
        if (players.isEmpty()) {
            throw new IllegalStateException("플레이어가 초기화되지 않았습니다.");
        }
        return players.get(currentPlayerIndex);
    }

    public static Color getCurrentPlayerColor() {
        return players.get(currentPlayerIndex).getColor();
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void previousPlayer() {
        currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
    }

    public List<Player> getPlayers() {
        return players;
    }
}
