package game.model;


import game.Position;
import game.core.factory.ChessPiece;
import game.model.state.SpecialMoveManager;
import game.model.state.MoveManager;

public class MoveValidator {
    private final SpecialMoveManager specialMoveManager;
    private final GameLogicActions gameLogicActions;
    private final MoveManager moveManager;

    public MoveValidator(SpecialMoveManager specialMoveManager, GameLogicActions gameLogicActions, MoveManager moveManager) {
        this.specialMoveManager = specialMoveManager;
        this.gameLogicActions = gameLogicActions;
        this.moveManager = moveManager;
    }

    public boolean isValidMove(ChessPiece piece, Position targetPosition) {
        if (piece == null) {
            return false;
        }
        if (gameLogicActions.isKingInCheck(piece.getColor()) &&
                !canMoveBreakCheck(piece, targetPosition)) {
            return false;
        }
        return moveManager.isAvailableMoveTarget(targetPosition, gameLogicActions);
    }

    private boolean canMoveBreakCheck(ChessPiece piece, Position targetPosition) {
        Position originalPosition = piece.getPosition();
        piece.setPosition(targetPosition);
        boolean breaksCheck = !gameLogicActions.isKingInCheck(piece.getColor());
        piece.setPosition(originalPosition);
        return breaksCheck;
    }
}