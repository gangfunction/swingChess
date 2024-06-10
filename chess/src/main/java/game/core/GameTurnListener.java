package game.core;

import game.observer.Observer;

public interface GameTurnListener {

    void addObserver(Observer observer);

    void notifyObservers(String message);

    void nextTurn();

    boolean isGameEnded();

    void endGame();

    String serializeGameState();

    void deserializeGameState(String gameState);
}
