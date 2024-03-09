package hello;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private Player currentPlayer; // 현재 차례의 플레이어
    private List<Player> players; // 게임에 참여하는 플레이어 목록
    private boolean gameEnded; // 게임 종료 여부

    public GameState() {
        this.players = new ArrayList<>();
        this.gameEnded = false;
    }

    // 플레이어를 게임에 추가하는 메서드
    public void addPlayer(Player player) {
        if (players.size() < 2) { // 체스는 2명의 플레이어만 가능
            this.players.add(player);
            if (players.size() == 1) {
                // 첫 번째 추가된 플레이어를 현재 차례로 설정
                this.currentPlayer = player;
            }
        }
    }

    // 게임의 상태를 초기화하는 메서드
    public void resetGame() {
        this.gameEnded = false;
        // 체스판 초기화 로직 추가 필요
    }

    // 다음 플레이어의 차례로 넘기는 메서드
    public void nextTurn() {
        if (!gameEnded) {
            currentPlayer = (currentPlayer == players.get(0)) ? players.get(1) : players.get(0);
        }
    }

    // 게임 종료 메서드
    public void endGame() {
        this.gameEnded = true;
    }

    // 현재 차례의 플레이어를 가져오는 메서드
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    // 게임 종료 여부를 가져오는 메서드
    public boolean isGameEnded() {
        return gameEnded;
    }

    // 게임에 참여하는 플레이어 목록을 가져오는 메서드
    public List<Player> getPlayers() {
        return players;
    }
}