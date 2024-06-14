package game.model.castling;

import game.Position;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
@Slf4j
public class CastlingUtils {
    private CastlingUtils() {
      log.info("CastlingUtils created");
    }
    public static List<Position> getKingSideCastlingPath(Position kingPosition) {
        List<Position> path = new ArrayList<>();
        for (int x = kingPosition.x() + 1; x <= 6; x++) {
            path.add(new Position(x, kingPosition.y()));
        }
        return path;
    }

    public static List<Position> getQueenSideCastlingPath(Position kingPosition) {
        List<Position> path = new ArrayList<>();
        for (int x = kingPosition.x() - 1; x >= 2; x--) {
            path.add(new Position(x, kingPosition.y()));
        }
        return path;
    }
}
