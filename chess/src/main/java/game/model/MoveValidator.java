package game.model;


import game.Position;
import game.core.factory.ChessPiece;

public class MoveValidator {
    private final GameStatusListener gameStatusListener;
    private final GameLogicActions gameLogicActions;

    public MoveValidator(GameStatusListener gameStatusListener, GameLogicActions gameLogicActions) {
        this.gameStatusListener = gameStatusListener;
        this.gameLogicActions = gameLogicActions;
    }

    public boolean isValidMove(ChessPiece piece, Position targetPosition) {
        if (piece == null) {
            return false;
        }
        if (gameLogicActions.isKingInCheck(piece.getColor()) &&
                !canMoveBreakCheck(piece, targetPosition)) {
            return false;
        }
        return gameStatusListener.isAvailableMoveTarget(targetPosition, gameLogicActions);
    }

    private boolean canMoveBreakCheck(ChessPiece piece, Position targetPosition) {
        Position originalPosition = piece.getPosition();
        piece.setPosition(targetPosition);
        boolean breaksCheck = !gameLogicActions.isKingInCheck(piece.getColor());
        piece.setPosition(originalPosition);
        return breaksCheck;
    }
}