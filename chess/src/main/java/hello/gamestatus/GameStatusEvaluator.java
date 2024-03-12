package hello.gamestatus;

import hello.GameUtils;
import hello.core.Player;
import hello.gameobject.ChessBoardUI;
import hello.gameobject.ChessGameState;

public class GameStatusEvaluator {
    private VictoryCondition victoryCondition;
    private DrawCondition drawCondition;
    private CheckStatus checkStatus;
    private final ChessGameState chessGameState;

    private ChessBoardUI chessBoardUI;

    private Player.Color currentPlayerColor;

    public GameStatusEvaluator(ChessGameState chessGameState) {
        this.chessGameState = chessGameState;
    }

//    public GameStatusEvaluator( chessBoard, ChessGameState chessGameState) {
//        this.chessGameState = chessGameState;
//        this.victoryCondition = new VictoryCondition(chessGameState, new GameUtils(), new CheckStatus(chessBoard, null));
//        this.drawCondition = new DrawCondition(chessBoard);
//        this.checkStatus = new CheckStatus(chessBoard, victoryCondition);
//    }

//    public GameStatus evaluateGameStatus() {
//        if (victoryCondition.isCheckmate(currentPlayerColor, chessBoard.getBoard(), chessBoardUI)) {
//            return GameStatus.CHECKMATE;
//        }
//        if (drawCondition.isStalemate()) {
//            return GameStatus.STALEMATE;
//        }
//        // 기타 상태 평가 로직 ...
//
//        return GameStatus.ONGOING; // 기본적으로 게임은 진행 중 상태입니다.
//    }
}

