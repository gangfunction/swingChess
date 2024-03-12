package game.observer;

public interface Observer {
    void update(String gameState);
    void logAction(String message);
}
