package game.core;

public interface GameTurnListener {

    void nextTurn();

    boolean isGameEnded();

    void endGame();

    String serializeGameState();

    String computerSerializeGameState();

    void deserializeGameState(String gameState);
}
