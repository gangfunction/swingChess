package game.core;

import game.util.Color;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PlayerManager {
    private final List<Player> players;
    @Setter
    private  int currentPlayerIndex;

    public PlayerManager() {
        players = new ArrayList<>();
    }

    public  void addPlayer(String name, Color color) {
        Player player = new Player(name, color);
        this.players.add(player);
    }


    public Player getCurrentPlayer() {
        if (players.isEmpty()){
            throw new IllegalStateException("플레이어가 초기화되지 않았습니다.");
        }
        return getPlayers().get(currentPlayerIndex);
    }

    public  Color getCurrentPlayerColor() {
        return players.get(currentPlayerIndex).color();
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % 2;
    }

    public void setCurrentPlayer(Player player) {
        currentPlayerIndex = players.indexOf(player);
    }
}
