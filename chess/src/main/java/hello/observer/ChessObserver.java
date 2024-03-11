package hello.observer;

import hello.core.ChessGame;

import java.util.ArrayList;
import java.util.List;

public class ChessObserver implements Subject{
    private List<Observer> observers;
    private String gameState;
    private ChessGame game;

    public ChessObserver() {
        observers = new ArrayList<>();
    }
    public ChessObserver(ChessGame game) {
        this();
        this.game = game;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
        notifyObservers();
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    public String getGameState() {
        return gameState;
    }

}
