package game.factory;

import game.Position;
import game.core.Color;
import game.object.GameStatusListener;
import game.strategy.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChessPiece {
    private static final Logger LOGGER = Logger.getLogger(ChessPiece.class.getName());
    @Getter
    @Setter
    private PieceType pieceType;
    @Getter
    private Position position;
    @Getter
    @Setter
    private Color color;
    @Getter
    private boolean moved = false;
    private final MoveStrategy moveStrategy;

    public ChessPiece(PieceType pieceType, Position position, Color color) {
        this.pieceType = pieceType;
        this.position = position;
        this.color = color;
        this.moveStrategy = MoveStrategyFactory.createMoveStrategy(pieceType);
    }

    public Set<Position> calculateMoves(GameStatusListener chessGameState) {
        return moveStrategy.calculateMoves(chessGameState, this);
    }

    public void setMoved(boolean moved) {
        LOGGER.log(Level.FINE, "{0} set to {1}", new Object[]{pieceType, moved ? "moved" : "not moved"});
        this.moved = moved;
    }

    public void setPosition(Position position) {
        LOGGER.log(Level.FINE, "Setting position of {0} to {1}", new Object[]{this, position});
        this.position = position;
    }

    public PieceType getType() {
        return pieceType;
    }
}
