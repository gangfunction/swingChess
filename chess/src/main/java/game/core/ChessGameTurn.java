package game.core;

import java.util.ArrayList;
import java.util.List;

public class ChessGameTurn {
    private final List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0; // 현재 차례인 플레이어의 인덱스
    private boolean gameEnded; // 게임 종료 여부

    public ChessGameTurn() {
        players.add(new Player(Player.Color.WHITE));
        players.add(new Player(Player.Color.BLACK));
        gameEnded = false;

    }

    // 다음 플레이어의 차례로 넘어가는 메서드
    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size(); // 리스트의 다음 인덱스로 이동. 플레이어 수를 초과하는 경우 0으로 되돌림
    }

    // 게임의 상태를 초기화하는 메서드
    public void resetGame() {
        this.gameEnded = false;
        // 추가: 체스판 초기화 및 기타 시작에 필요한 로직 수행
    }

    // 게임 종료 메서드
    public void endGame() {
        this.gameEnded = true;
    }

    // 현재 차례인 플레이어를 가져오는 메서드
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    // 현재 차례인 플레이어의 정보를 출력하는 메서드
    public void printCurrentPlayer() {
        Player currentPlayer = getCurrentPlayer();
        System.out.println("It's " + currentPlayer.getName() + "'s turn. (" + currentPlayer.getColor() + ")");
    }

    public Player.Color getCurrentPlayerColor() {
        return getCurrentPlayer().getColor();
    }
}
