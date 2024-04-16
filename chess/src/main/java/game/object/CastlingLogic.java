package game.object;

import game.Position;
import game.factory.ChessPiece;
import game.factory.PieceType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CastlingLogic {
    private  GameLogicActions gameLogicActions;
    private  GameStatusListener gameStatusListener;
    private final GameEventListener gameEventListener;


    public void setCastlingLogic(GameStatusListener chessGameState, GameLogicActions gameLogicActions) {
        this.gameStatusListener = chessGameState;
        this.gameLogicActions = gameLogicActions;
    }

    public CastlingLogic(GameEventListener gameEventListener){
        this.gameEventListener = gameEventListener;
    }


    protected void castlingJudgeLogic(ChessPiece piece, Position clickedPosition) {
        System.out.println("CastlingLogic.castlingJudgeLogic");
        List<ChessPiece> opponentPieces = getOpponentPieces(piece);
        List<Position> path = gameLogicActions.getPositions(piece, clickedPosition);

        if (!isCastlingAttempt(piece, clickedPosition) && isPathClearAndSafe(path, opponentPieces)) {
            return;
        }
        executeCastling(piece);
    }

    private boolean isCastlingAttempt(ChessPiece piece, Position clickedPosition) {
        return piece.getType() == PieceType.KING && !piece.isMoved()
                && gameStatusListener.isRookUnmovedForCastling(piece.getColor(), clickedPosition)
                && !gameLogicActions.isKingInCheck(piece.getColor());
    }

    private List<ChessPiece> getOpponentPieces(ChessPiece piece) {
        System.out.println("CastlingLogic.getOpponentPieces");
        return gameStatusListener.getChessPieces().stream()
                .filter(opponentPiece -> opponentPiece.getColor() != piece.getColor())
                .collect(Collectors.toList());
    }

    private boolean isPathClearAndSafe(List<Position> path, List<ChessPiece> opponentPieces) {
        System.out.println("CastlingLogic.isPathClearAndSafe");
        for (Position position : path) {
            if (gameStatusListener.getChessPieceAt(position).isPresent() || isPositionUnderAttack(position, opponentPieces)) {
                System.out.println("CastlingLogic.isPathClearAndSafe: false");
                return false;
            }
        }
        return true;
    }

    private boolean isPositionUnderAttack(Position position, List<ChessPiece> opponentPieces) {
        System.out.println("CastlingLogic.isPositionUnderAttack");
        return opponentPieces.stream()
                .anyMatch(opponentPiece -> gameLogicActions.calculateMovesForPiece(opponentPiece).contains(position));
    }
    private void executeCastling(ChessPiece king) {
        System.out.println("CastlingLogic.executeCastling");
        if(isQueenSideCastlingPathClear(king) || isKingSideCastlingPathClear(king)){
            gameLogicActions.setQueenCastleSide(true);
            gameLogicActions.setKingCastleSide(true);
        }
        Position kingTargetPosition = isQueenSideCastlingPathClear(king) ?
                new Position(6, king.getPosition().y()) : new Position(2, king.getPosition().y());
        Position rookTargetPosition = isQueenSideCastlingPathClear(king) ?
                new Position(5, king.getPosition().y()) : new Position(3, king.getPosition().y());

        gameEventListener.highlightMoves(List.of(kingTargetPosition, rookTargetPosition));
        gameStatusListener.setCanCastle(true);
    }
    private boolean isQueenSideCastlingPathClear(ChessPiece king) {
        List<Position> pathToCheck = getQueenSideCastlingPath(king);
        List<ChessPiece> opponentPieces = getOpponentPieces(king);
        return !isPathClearAndSafe(pathToCheck, opponentPieces);
    }
    private boolean isKingSideCastlingPathClear(ChessPiece king){
        List<Position> pathToCheck = getKingSideCastlingPath(king);
        List<ChessPiece> opponentPieces = getOpponentPieces(king);
        return !isPathClearAndSafe(pathToCheck, opponentPieces);
    }

    private List<Position> getKingSideCastlingPath(ChessPiece king) {
        Position kingPosition = king.getPosition();
        List<Position> path = new ArrayList<>();
        // 킹사이드 캐슬링 경로는 왕의 위치에서 오른쪽으로 2칸(룩의 위치까지)입니다.
        for (int x = kingPosition.x() + 1; x <= 7; x++) {
            path.add(new Position(x, kingPosition.y()));
            if (x == 7) break; // 룩의 위치에 도달하면 반복을 중단합니다.
        }
        return path;
    }

    private List<Position> getQueenSideCastlingPath(ChessPiece king) {
        Position kingPosition = king.getPosition();
        List<Position> path = new ArrayList<>();
        // 퀸사이드 캐슬링 경로는 왕의 위치에서 왼쪽으로 4칸(룩의 위치까지)입니다.
        for (int x = kingPosition.x() - 1; x >= 0; x--) {
            path.add(new Position(x, kingPosition.y()));
            if (x == 0) break; // 룩의 위치에 도달하면 반복을 중단합니다.
        }
        return path;
    }

}
