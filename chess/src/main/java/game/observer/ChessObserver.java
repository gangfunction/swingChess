package game.observer;

import game.core.ChessGameTurn;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ChessObserver implements Subject{
    private final List<Observer> observers;
    @Getter
    private String gameState;
    private ChessGameTurn game;

    public ChessObserver() {
        observers = new ArrayList<>();
    }

    public void setGameState(String gameState) {
        if(this.gameState == null || !this.gameState.equals(gameState)){
            this.gameState = gameState;
            notifyObservers();
        }
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
            observer.update(gameState);
        }
    }

}
