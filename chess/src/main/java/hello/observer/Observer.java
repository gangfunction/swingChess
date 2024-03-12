package hello.observer;

public interface Observer {
    void update(String gameState);
    void logAction(String message);
}
