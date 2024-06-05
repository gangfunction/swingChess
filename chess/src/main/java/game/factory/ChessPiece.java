package game.factory;

import game.GameUtils;
import game.Position;
import game.core.Color;
import game.object.GameStatusListener;
import game.strategy.*;
import lombok.Getter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChessPiece {
    private static final Logger LOGGER = Logger.getLogger(ChessPiece.class.getName());
    private final PieceType pieceType;
    @Getter
    private Position position;
    @Getter
    private final Color color;
    @Getter
    private boolean moved = false;
    private final MoveStrategy moveStrategy;

    public ChessPiece(PieceType pieceType, Position position, Color color) {
        this.pieceType = pieceType;
        this.position = position;
        this.color = color;
        this.moveStrategy = createMoveStrategy(pieceType);
    }

    private MoveStrategy createMoveStrategy(PieceType pieceType) {
        return switch (pieceType) {
            case PAWN -> new PawnStrategy();
            case ROOK -> new RookStrategy();
            case KNIGHT -> new KnightStrategy();
            case BISHOP -> new BishopStrategy();
            case QUEEN -> new QueenStrategy();
            case KING -> new KingStrategy();
        };
    }

    public List<Position> calculateMoves(GameStatusListener chessGameState, GameUtils utils) {
        return moveStrategy.calculateMoves(chessGameState, this, utils);
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