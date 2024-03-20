package game.core;

import game.observer.Observer;
import game.status.DrawCondition;

import java.util.List;

public interface GameTurnListener {
    void addObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers(String message);



    // 다음 플레이어의 차례로 넘어가는 메서드
    void nextTurn();

    void resetGame();

    boolean isGameEnded();

    // 게임 종료 메서드
    void endGame();

    // 현재 차례인 플레이어를 가져오는 메서드
    Player getCurrentPlayer();

    Color getCurrentPlayerColor();


    String serializeGameState();
}
