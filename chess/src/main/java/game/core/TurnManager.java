package game.core;

public interface TurnManager{
    void nextTurn();
    Player getCurrentPlayer();
    boolean isGameEnded();
    void resetGame();
}
