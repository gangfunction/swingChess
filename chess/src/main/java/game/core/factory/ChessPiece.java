package game.core.factory;

import game.Position;
import game.model.state.ChessPieceManager;
import game.model.state.MoveManager;
import game.util.Color;
import game.strategy.*;
import game.util.MoveStrategyFactory;
import game.util.PieceType;
import lombok.Getter;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChessPiece {
    private static final Logger LOGGER = Logger.getLogger(ChessPiece.class.getName());
    @Getter
    private PieceType pieceType;
    @Getter
    private Position position;
    @Getter
    private Color color;
    @Getter
    private boolean moved = false;
    @Getter
    private final MoveStrategy moveStrategy;

    public ChessPiece(PieceType pieceType, Position position, Color color) {
        this.pieceType = pieceType;
        this.position = position;
        this.color = color;
        this.moveStrategy = MoveStrategyFactory.createMoveStrategy(pieceType);
    }

    public Set<Position> calculateMoves(ChessPieceManager chessPieceManager, MoveManager moveManager) {
        return moveStrategy.calculateMoves(chessPieceManager, moveManager,this);
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
