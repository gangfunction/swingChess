package game.object;

import game.observer.Observer;

public class GameUIObserver implements Observer {

    @Override
    public void update(String gameState) {
        System.out.println("Game state: " + gameState);
    }

}
