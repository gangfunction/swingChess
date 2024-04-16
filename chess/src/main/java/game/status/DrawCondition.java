package game.status;


import game.Position;
import game.core.Color;
import game.core.GameTurnListener;
import game.factory.ChessPiece;
import game.factory.PieceType;
import game.object.GameLogicActions;
import game.object.GameStatusListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrawCondition {
    private GameStatusListener gameStatusListener;
    private GameLogicActions gameLogicActions;
    private GameTurnListener gameTurnListener;

    public DrawCondition() {

    }

    public void setDrawCondition(GameStatusListener chessGameState, GameLogicActions chessGameLogic, GameTurnListener chessGameTurn) {
        this.gameStatusListener = chessGameState;
        this.gameLogicActions = chessGameLogic;
        this.gameTurnListener = chessGameTurn;
    }


    private final Map<String, Integer> gameStateOccurrences = new HashMap<>();
    public boolean isThreefoldRepetition() {
        String currentState = gameTurnListener.serializeGameState();

        // 현재 상태의 발생 횟수 업데이트
        gameStateOccurrences.put(currentState, gameStateOccurrences.getOrDefault(currentState, 0) + 1);

        // 현재 상태가 세 번 이상 발생했는지 확인
        return gameStateOccurrences.get(currentState) >= 3;
    }


    public boolean isStalemate(Color currentPlayerColor) {
        List<ChessPiece> currentPlayerPieces = gameStatusListener.getChessPieces().stream()
                .filter(piece -> piece.getColor() == currentPlayerColor)
                .toList();

        // 모든 말에 대해 가능한 모든 이동을 검사합니다.
        for (ChessPiece piece : currentPlayerPieces) {
            List<Position> possibleMoves = gameLogicActions.calculateMovesForPiece(piece);
            for (Position move : possibleMoves) {
                // 임시로 이동을 수행해봅니다.
                Position originalPosition = piece.getPosition();
                gameStatusListener.getChessPieceAt(move).ifPresent(gameStatusListener::removeChessPiece);
                piece.setPosition(move);
                piece.setMoved(true);
                // 체크 상태인지 확인합니다.
                boolean isInCheck = gameLogicActions.isKingInCheck(currentPlayerColor);
                // 원래 상태로 되돌립니다.
                gameStatusListener.getChessPieceAt(originalPosition).ifPresent(gameStatusListener::removeChessPiece);
                piece.setPosition(originalPosition);
                piece.setMoved(true);
                if (!isInCheck) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isInsufficientMaterial() {
        List<ChessPiece> pieces = gameStatusListener.getChessPieces();
        long nonKingCount = 0;
        long bishopCount = 0;
        Integer bishopColor = null;
        boolean isBishopColorConsistent = true;

        for (ChessPiece piece : pieces) {
            if (piece.getType() != PieceType.KING) {
                nonKingCount++;
                if (piece.getType() == PieceType.BISHOP) {
                    bishopCount++;
                    int color = (piece.getPosition().x() + piece.getPosition().y()) % 2;
                    if (bishopColor == null) {
                        bishopColor = color;
                    } else if (bishopColor != color) {
                        isBishopColorConsistent = false;
                    }
                } else if (piece.getType() == PieceType.KNIGHT && nonKingCount > 1) {
                    return false;
                }
            }
        }

        if (nonKingCount == 0) {
            return true;
        } else if (nonKingCount == 1) {
            return bishopCount == 1 || (bishopCount == 0 && pieces.stream().anyMatch(p -> p.getType() == PieceType.KNIGHT));
        } else {
            return bishopCount == nonKingCount && isBishopColorConsistent;
        }
    }
    public boolean isFiftyMoveRule() {
        // 최근 50회 이동 동안의 기록을 추적하는 리스트 또는 카운터가 필요합니다.
        // 여기서는 간단히 카운터를 사용하는 것으로 가정합니다.
        int moveWithoutPawnOrCapture = gameStatusListener.getMoveWithoutPawnOrCaptureCount();

        // 50회 이동 동안 폰의 이동이 없고 말이 잡히지 않았는지 체크
        return moveWithoutPawnOrCapture >= 50;
    }




    // 무승부 조건들을 종합하고, 현재 체스 게임이 무승부인지 판단하는 메서드
    public boolean isDraw(Color currentPlayerColor) {
        return isStalemate(currentPlayerColor) || isInsufficientMaterial() || isThreefoldRepetition() || isFiftyMoveRule();
    }
}
