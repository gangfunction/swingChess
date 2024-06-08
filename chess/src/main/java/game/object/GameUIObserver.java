package game.object;

import game.observer.Observer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameUIObserver implements Observer {

    @Override
    public void update(String gameState) {
        log.info("Game state updated to: {}", gameState);
    }

}
