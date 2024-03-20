package game.object;

import game.Position;
import game.factory.ChessPiece;
import game.factory.Type;

import java.util.List;
import java.util.stream.Collectors;

public class CastlingLogic {
    private GameLogicActions gameLogicActions;
    private  GameStatusListener gameStatusListener;
    private  GameEventListener gameEventListener;

    public void setCastlingLogic(GameLogicActions gameLogicActions, GameStatusListener gameStatusListener, GameEventListener gameEventListener) {
        this.gameLogicActions = gameLogicActions;
        this.gameStatusListener = gameStatusListener;
        this.gameEventListener = gameEventListener;
    }
    public CastlingLogic(){

    }


    protected void castlingJudgeLogic(ChessPiece piece, Position clickedPosition) {
        if (!isCastlingAttempt(piece, clickedPosition)) return;

        List<ChessPiece> opponentPieces = getOpponentPieces(piece);
        List<Position> path = gameLogicActions.getPositions(piece, clickedPosition);

        if (isPathClearAndSafe(path, opponentPieces)) {
            executeCastling(piece, clickedPosition);
        }
    }

    private boolean isCastlingAttempt(ChessPiece piece, Position clickedPosition) {
        return piece.getType() == Type.KING && !piece.isMoved()
                && gameStatusListener.isRookUnmovedForCastling(piece.getColor(), clickedPosition)
                && !gameLogicActions.isKingInCheck(piece.getColor());
    }

    private List<ChessPiece> getOpponentPieces(ChessPiece piece) {
        return gameStatusListener.getChessPieces().stream()
                .filter(opponentPiece -> opponentPiece.getColor() != piece.getColor())
                .collect(Collectors.toList());
    }

    private boolean isPathClearAndSafe(List<Position> path, List<ChessPiece> opponentPieces) {
        for (Position position : path) {
            if (gameStatusListener.getChessPieceAt(position).isPresent() || isPositionUnderAttack(position, opponentPieces)) {
                return false;
            }
        }
        return true;
    }

    private boolean isPositionUnderAttack(Position position, List<ChessPiece> opponentPieces) {
        return opponentPieces.stream()
                .anyMatch(opponentPiece -> gameLogicActions.calculateMovesForPiece(opponentPiece).contains(position));
    }

    private void executeCastling(ChessPiece king, Position clickedPosition) {
        Position kingTargetPosition = clickedPosition.x() > king.getPosition().x() ?
                new Position(6, king.getPosition().y()) : new Position(2, king.getPosition().y());
        Position rookTargetPosition = clickedPosition.x() > king.getPosition().x() ?
                new Position(5, king.getPosition().y()) : new Position(3, king.getPosition().y());

        gameEventListener.highlightMoves(List.of(kingTargetPosition, rookTargetPosition));
    }
}
