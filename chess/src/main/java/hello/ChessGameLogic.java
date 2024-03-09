package hello;

public class ChessGameLogic {
    public static boolean isValidMove(ChessPiece piece, int toX, int toY, ChessPiece[][] board) {
        return false;
    }
    public static boolean isCheckmate(Player.Color currentPlayerColor, ChessPiece[][] board) {
        // 1단계: 현재 플레이어의 왕의 위치 찾기
        ChessPiece king = findKing(currentPlayerColor, board);
        if (king == null) {
            return false; // 왕이 없는 경우는 체크메이트가 아님
        }

        // 2단계: 왕이 현재 체크 상태인지 확인
        if (!isInCheck(king, board)) {
            return false; // 왕이 체크 상태가 아니면 체크메이트가 아님
        }

        // 3단계: 왕이 체크 상태에서 벗어날 수 있는지 확인
        return !canKingEscape(king, board);
    }
    private static ChessPiece findKing(Player.Color color, ChessPiece[][] board) {
        for (ChessPiece[] chessPieces : board) {
            for (ChessPiece piece : chessPieces) {
                if (piece != null && piece.getType() == ChessPiece.Type.KING && piece.getColor() == color) {
                    return piece;
                }
            }
        }
        return null; // 왕을 찾지 못한 경우
    }
    private static boolean isInCheck(ChessPiece king, ChessPiece[][] board) {
        for (ChessPiece[] chessPieces : board) {
            for (ChessPiece piece : chessPieces) {
                // 현재 위치의 체스말이 상대방의 말이고, 그 말이 왕의 위치로 이동할 수 있는지 확인
                if (piece != null && piece.getColor() != king.getColor() && isValidMove(piece, king.getPosition().getX(), king.getPosition().getY(), board)) {
                    return true; // 상대방의 말이 왕을 공격할 수 있는 경우
                }
            }
        }
        return false; // 왕이 안전한 경우
    }
    private static boolean canKingEscape(ChessPiece king, ChessPiece[][] board) {
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };
        for (int[] direction : directions) {
            int newX = king.getPosition().getX() + direction[0];
            int newY = king.getPosition().getY() + direction[1];

            // 해당 위치가 보드 내에 있는지 확인
            if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length) {
                // 해당 위치로 이동이 가능한지 확인
                if (isValidMove(king, newX, newY, board)) {
                    // 임시로 이동 실행
                    Position originalPosition = king.getPosition();
                    king.setPosition(new Position(newX, newY));
                    board[originalPosition.getX()][originalPosition.getY()] = null;
                    board[newX][newY] = king;

                    // 이동 후 왕이 체크 상태인지 확인
                    boolean isInCheckAfterMove = isInCheck(king, board);

                    // 이동을 원래대로 되돌림
                    king.setPosition(originalPosition);
                    board[newX][newY] = null;
                    board[originalPosition.getX()][originalPosition.getY()] = king;

                    // 이동 후 왕이 체크 상태가 아니라면, 체크에서 벗어날 수 있음
                    if (!isInCheckAfterMove) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
