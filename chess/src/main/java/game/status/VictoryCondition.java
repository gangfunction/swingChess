package game.status;

import game.GameUtils;
import game.Position;
import game.core.Player;
import game.object.ChessBoardUI;
import game.object.ChessGameState;
import game.object.ChessPiece;
import game.strategy.KingStrategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VictoryCondition {

    private final ChessGameState chessGameState;
    private final GameUtils gameUtils;

    private final CheckStatus checkStatus;

    public VictoryCondition(ChessGameState chessGameState, GameUtils gameUtils, CheckStatus checkStatus) {
        this.chessGameState = chessGameState;
        this.gameUtils = gameUtils;
        this.checkStatus = checkStatus;
    }

    public  boolean canKingEscape(ChessPiece king, List<ChessPiece> chessPieces) {
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };
// 현재 보드의 크기 표준 8x8 상수로 설정
        final int boardSize = 8;

        for (int[] direction : directions) {
            int newX = king.getPosition().getX() + direction[0];
            int newY = king.getPosition().getY() + direction[1];

            // 해당 위치가 보드 내에 있는지 확인
            if (newX >= 0 && newX < boardSize && newY >= 0 && newY < boardSize) {
                // 해당 위치로 이동이 가능한지 확인
                if (isValidMove(king, newX, newY,chessGameState)) {
                    // 임시로 이동 실행하고 체크 상태 확인을 위한 로직
                    Position originalPosition = king.getPosition();
                    king.setPosition(new Position(newX, newY));

                    // 체스말을 리스트에서 찾아 재배치
                    chessPieces.remove(king);
                    chessPieces.add(new ChessPiece(king.getType(), new Position(newX, newY), king.getColor()));

                    // 이동 후 왕이 체크 상태인지 확인
                    boolean isInCheckAfterMove = checkStatus.isInCheck(king, chessPieces, chessGameState);

                    // 이동을 원래대로 되돌림
                    king.setPosition(originalPosition);
                    chessPieces.remove(king);
                    chessPieces.add(new ChessPiece(king.getType(), originalPosition, king.getColor()));

                    // 이동 후 왕이 체크 상태가 아니라면, 체크에서 벗어날 수 있음
                    if (!isInCheckAfterMove) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public boolean isCheckmate(Player.Color currentPlayerColor, List<ChessPiece> chessPieces, ChessBoardUI chessBoardUI) {
        // 1단계: 현재 플레이어의 왕의 위치 찾기
        ChessPiece king = CheckStatus.findKing(currentPlayerColor, chessPieces);
        if (king == null) {
            return false; // 왕이 없는 경우는 체크메이트가 아님
        }

        // 2단계: 왕이 현재 체크 상태인지 확인
        if (!checkStatus.isInCheck(king, chessPieces, chessGameState)) {
            return false; // 왕이 체크 상태가 아니면 체크메이트가 아님
        }

        // 3단계: 왕이 체크 상태에서 벗어날 수 있는지 확인
        return !canKingEscape(king, chessPieces);
    }

     boolean isValidMove(ChessPiece king, int newX, int newY, ChessGameState chessGameState) {
        {
            Set<Position> validMoves = new HashSet<>(new KingStrategy().calculateMoves(chessGameState, king, gameUtils));

            Position newPosition = new Position(newX, newY);
            return validMoves.contains(newPosition);
            //TODO: 해당 메서드 연결
        }

    }
}
