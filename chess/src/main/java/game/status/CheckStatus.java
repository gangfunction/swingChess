package game.status;

import game.core.Player;
import game.object.ChessGameState;
import game.object.ChessPiece;

import java.util.List;

public class CheckStatus {
    private final VictoryCondition victoryCondition;

    public CheckStatus(VictoryCondition victoryCondition) {
        this.victoryCondition = victoryCondition;
    }

    public  boolean isInCheck(ChessPiece king, List<ChessPiece> chessPieces, ChessGameState chessGameState) {
        for (ChessPiece piece : chessPieces) {
            // 현재 위치의 체스말이 상대방의 말이고, 그 말이 왕의 위치로 이동할 수 있는지 확인
            if (piece != null && piece.getColor() != king.getColor() &&
                    victoryCondition.isValidMove(piece, king.getPosition().getX(), king.getPosition().getY(), chessGameState)) {
                return true; // 상대방의 말이 왕을 공격할 수 있는 경우
            }
        }
        return false; // 왕이 안전한 경우
    }

    public static ChessPiece findKing(Player.Color color,List<ChessPiece> chessPieces) {
            for (ChessPiece piece : chessPieces) {
                if (piece != null && piece.getType() == ChessPiece.Type.KING && piece.getColor() == color) {
                    return piece;
                }
        }
        return null; // 왕을 찾지 못한 경우
    }

    // 체크메이트 여부를 확인하는 메서드
    public static boolean isCheckmated(Player.Color currentPlayerColor, ChessPiece[][] board) {
        // 해당 메서드 구현
        // VictoryCondition에서 논리를 가져오거나 새로 작성해야 할 수 있음
        return false;
    }

    // 스테일메이트 여부를 확인하는 메서드
    public static boolean isStalemated(Player.Color currentPlayerColor, ChessPiece[][] board) {
        // 해당 메서드 구현
        // 이동 가능한 모든 경우의 수를 확인하고 체크 상태가 아닐 때, 합법적인 이동이 없는지 확인

        return false;
    }
}
