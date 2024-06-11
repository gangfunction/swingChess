package game.core;

public interface GameTurnListener {


    void nextTurn();

    boolean isGameEnded();

    void endGame();

    String serializeGameState();

    void deserializeGameState(String gameState);
}
