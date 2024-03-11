package hello.gamestatus;

import hello.core.Player;
import hello.gameobject.ChessBoard;

public class GameStatusEvaluator {
    private VictoryCondition victoryCondition;
    private DrawCondition drawCondition;
    private CheckStatus checkStatus;
    private ChessBoard chessBoard;

    private Player.Color currentPlayerColor;

    public GameStatusEvaluator(ChessBoard chessBoard) {
        this.victoryCondition = new VictoryCondition(chessBoard);
        this.drawCondition = new DrawCondition(chessBoard);
        this.checkStatus = new CheckStatus(chessBoard);
    }

    public GameStatus evaluateGameStatus() {
        if (victoryCondition.isCheckmate(currentPlayerColor, chessBoard.getBoard(), chessBoard)) {
            return GameStatus.CHECKMATE;
        }
        if (drawCondition.isStalemate()) {
            return GameStatus.STALEMATE;
        }
        // 기타 상태 평가 로직 ...

        return GameStatus.ONGOING; // 기본적으로 게임은 진행 중 상태입니다.
    }
}

