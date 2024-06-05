package game.object;

import game.Position;
import game.factory.ChessPiece;
import game.factory.PieceType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CastlingLogic {
    private GameLogicActions gameLogicActions;
    private GameStatusListener gameStatusListener;
    private final GameEventListener gameEventListener;
    @Getter
    private boolean isQueenSide;

    public CastlingLogic(GameEventListener gameEventListener) {
        this.gameEventListener = gameEventListener;
    }

    public void setCastlingLogic(GameStatusListener chessGameState, GameLogicActions gameLogicActions) {
        this.gameStatusListener = chessGameState;
        this.gameLogicActions = gameLogicActions;
    }

    protected void castlingJudgeLogic(ChessPiece piece, Position clickedPosition) {
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
        return piece.getType() == PieceType.KING && piece.isMoved()
                && gameStatusListener.isRookUnmovedForCastling(piece.getColor(), clickedPosition)
                && !gameLogicActions.isKingInCheck(piece.getColor())
                && !gameLogicActions.isKingInCheckAfterMove(piece, clickedPosition); // 추가 조건
    }

    private List<ChessPiece> getOpponentPieces(ChessPiece piece) {
        System.out.println("CastlingLogic.getOpponentPieces");
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
        gameEventListener.highlightMoves(List.of(kingTargetPosition, rookTargetPosition));
        gameStatusListener.setCanCastle(true);

    }

    private boolean isCastlingPathClear(ChessPiece king, boolean isQueenSide) {
        List<Position> pathToCheck = isQueenSide ? getQueenSideCastlingPath(king) : getKingSideCastlingPath(king);
        List<ChessPiece> opponentPieces = getOpponentPieces(king);
        return isPathClearAndSafe(pathToCheck, opponentPieces);
    }

    private List<Position> getKingSideCastlingPath(ChessPiece king) {
        Position kingPosition = king.getPosition();
        List<Position> path = new ArrayList<>();
        for (int x = kingPosition.x() + 1; x <= 6; x++) {
            path.add(new Position(x, kingPosition.y()));
        }
        return path;
    }

    private List<Position> getQueenSideCastlingPath(ChessPiece king) {
        Position kingPosition = king.getPosition();
        List<Position> path = new ArrayList<>();
        for (int x = kingPosition.x() - 1; x >= 2; x--) {
            path.add(new Position(x, kingPosition.y()));
        }
        return path;
    }
}