package game.core;

public interface GameTurnListener {

    void nextTurn();

    void endGame();

    String serializeGameState();

    String computerSerializeGameState();

    void deserializeGameState(String gameState);

}
