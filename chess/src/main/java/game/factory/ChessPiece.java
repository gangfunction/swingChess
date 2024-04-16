package game.factory;


import game.GameUtils;
import game.Position;
import game.core.Color;
import game.object.GameStatusListener;
import game.strategy.*;

import java.util.List;
import java.util.logging.Logger;

public class ChessPiece {
    private static final Logger LOGGER = Logger.getLogger(ChessPiece.class.getName());
    private PieceType pieceType;
    private Position position;
    private Color color;
    private boolean moved = false;

    public boolean isMoved() {
        return moved;
    }

    private MoveStrategy moveStrategy;

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
        LOGGER.fine(this.pieceType + "set to" + (moved ? "moved" : "not moved"));
        this.moved = moved;
    }

    public ChessPiece() {
    }

    public void setPosition(Position position) {
        LOGGER.fine("Setting position of " + this + " to " + position);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

    public PieceType getType() {
        return pieceType;
    }
}