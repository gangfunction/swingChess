package game.model.castling;

import game.Position;
import game.core.factory.ChessPiece;
import game.model.state.ChessPieceManager;
import game.util.PieceType;
import game.ui.GameEventListener;
import game.model.GameLogicActions;
import game.model.state.SpecialMoveManager;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CastlingLogic {
    private GameLogicActions gameLogicActions;
    private SpecialMoveManager specialMoveManager;
    private final GameEventListener gameEventListener;
    @Getter
    private boolean isQueenSide;
    private final ChessPieceManager chessPieceManger;

    public CastlingLogic(GameEventListener gameEventListener, ChessPieceManager chessPieceManger) {
        this.gameEventListener = gameEventListener;
        this.chessPieceManger = chessPieceManger;
    }

    public void setCastlingLogic(SpecialMoveManager chessGameState, GameLogicActions gameLogicActions) {
        this.specialMoveManager = chessGameState;
        this.gameLogicActions = gameLogicActions;
    }

    public void castlingJudgeLogic(ChessPiece piece, Position clickedPosition) {
        if (piece.getType() != PieceType.KING) {
            return; // 캐슬링은 왕만 할 수 있습니다.
        }
        isQueenSide = clickedPosition.x() < piece.getPosition().x();
        boolean isPathClear = isCastlingPathClear(piece, isQueenSide);

        if (isCastlingAttempt(piece, clickedPosition) && isPathClear) {
            executeCastling(piece, isQueenSide);
        }
    }

    private boolean isCastlingAttempt(ChessPiece piece, Position clickedPosition) {
        return piece.getType() == PieceType.KING && !piece.isMoved()
                && specialMoveManager.isRookUnmovedForCastling(piece.getColor(), clickedPosition)
                && !gameLogicActions.isKingInCheck(piece.getColor())
                && !gameLogicActions.isKingInCheckAfterMove(piece, clickedPosition)
                && isCastlingPathClear(piece, isQueenSide); // 추가 조건
    }

    private List<ChessPiece> getOpponentPieces(ChessPiece piece) {
        return chessPieceManger.getChessPieces().values().stream()
                .filter(opponentPiece -> opponentPiece.getColor() != piece.getColor())
                .collect(Collectors.toList());
    }

    private boolean isPathClearAndSafe(List<Position> path, List<ChessPiece> opponentPieces) {
        for (Position position : path) {
            if (chessPieceManger.getChessPieceAt(position) != null || isPositionUnderAttack(position, opponentPieces)) {
                return false;
            }
        }
        return true;
    }

    private boolean isPositionUnderAttack(Position position, List<ChessPiece> opponentPieces) {
        return opponentPieces.stream()
                .anyMatch(opponentPiece -> gameLogicActions.calculateMovesForPiece(opponentPiece).contains(position));
    }

    private void executeCastling(ChessPiece king, boolean isQueenSide) {
        Position kingTargetPosition;
        Position rookTargetPosition;

        if (isQueenSide) {
            kingTargetPosition = new Position(2, king.getPosition().y());
            rookTargetPosition = new Position(3, king.getPosition().y());
        } else {
            kingTargetPosition = new Position(6, king.getPosition().y());
            rookTargetPosition = new Position(5, king.getPosition().y());
        }
        gameEventListener.highlightMoves(Set.of(kingTargetPosition, rookTargetPosition));
        specialMoveManager.setCanCastle(true);

    }
    private boolean isCastlingPathClear(ChessPiece king, boolean isQueenSide) {
        List<Position> pathToCheck = isQueenSide ? CastlingUtils.getQueenSideCastlingPath(king.getPosition()) : CastlingUtils.getKingSideCastlingPath(king.getPosition());
        List<ChessPiece> opponentPieces = getOpponentPieces(king);
        return isPathClearAndSafe(pathToCheck, opponentPieces);
    }

}