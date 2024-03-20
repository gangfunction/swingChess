package game.core;

import java.util.ArrayList;
import java.util.List;

public class ChessGameTurn implements TurnManager{
    private List<Player> players = new ArrayList<>();
    private static final int NUMBER_OF_PLAYERS = 2; // 플레이어 수
    private int currentPlayerIndex; // 현재 차례인 플레이어의 인덱스
    private boolean gameEnded; // 게임 종료 여부

    public ChessGameTurn() {
        this.players=initializePlayers();
        this.currentPlayerIndex = 0;
        this.gameEnded = false;

    }
    private List<Player> initializePlayers(){
        players.add(new Player("pin",Color.WHITE));
        players.add(new Player("jake",Color.BLACK));
        return players;
    }

    // 다음 플레이어의 차례로 넘어가는 메서드
    @Override
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % NUMBER_OF_PLAYERS; // 리스트의 다음 인덱스로 이동. 플레이어 수를 초과하는 경우 0으로 되돌림
    }
    @Override
    public void resetGame() {
        this.gameEnded = false;
        // 추가: 체스판 초기화 및 기타 시작에 필요한 로직 수행
    }
    @Override
    public boolean isGameEnded() {
        return gameEnded;
    }

    // 게임 종료 메서드
    public void endGame() {
        this.gameEnded = true;
    }

    // 현재 차례인 플레이어를 가져오는 메서드
    public Player getCurrentPlayer() {
        Player currentPlayer = players.get(currentPlayerIndex);
        System.out.println("It's " + currentPlayer + "'s turn. (" + currentPlayer.getColor() + ")");

        return currentPlayer;
    }

    public Color getCurrentPlayerColor() {
        return getCurrentPlayer().getColor();
    }
}
