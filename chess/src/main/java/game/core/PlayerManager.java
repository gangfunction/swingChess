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
        System.out.println("현재 플레이어의 색깔을 반환합니다." + players.get(currentPlayerIndex).getColor());
        return players.get(currentPlayerIndex).getColor();
    }

    public void nextPlayer() {
        System.out.println("다음 플레이어로 넘어갑니다."+ players.size());
        currentPlayerIndex = (currentPlayerIndex + 1) % 2;
    }

    public void setCurrentPlayer(Player player) {
        System.out.println("현재 플레이어를 설정합니다." + player.getName() + " " + player.getColor());
        currentPlayerIndex = players.indexOf(player);
    }
}
