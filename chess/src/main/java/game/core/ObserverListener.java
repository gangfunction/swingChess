package game.core;

import game.observer.Observer;

public interface ObserverListener {
    void addObserver(Observer observer);
    void notifyObservers(String message);
}
