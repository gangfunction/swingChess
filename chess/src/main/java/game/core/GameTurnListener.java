package game.core;

import game.observer.Observer;
import game.status.DrawCondition;

import java.util.List;

public interface GameTurnListener {

    void addObserver(Observer observer);

    void notifyObservers(String message);

    void nextTurn();

    boolean isGameEnded();

    void endGame();

    Player getCurrentPlayer();


    Color getCurrentPlayerColor();

    String serializeGameState();

    void deserializeGameState(String gameState);
}
