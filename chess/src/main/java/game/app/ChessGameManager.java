package game.app;

import game.Position;
import game.command.CommandInvoker;
import game.core.ChessGameTurn;
import game.factory.ChessPiece;
import game.object.ChessBoardUI;
import game.object.ChessGameLogic;
import game.object.ChessGameState;
import game.object.PromotionLogic;
import game.object.castling.CastlingLogic;
import game.status.DrawCondition;
import game.status.VictoryCondition;

import javax.swing.*;
import java.util.Map;

public class ChessGameManager {
    public static ChessBoardUI chessBoardUI;
    private static ChessGameState chessGameState;
    static ChessGameTurn chessGameTurn;
    static CommandInvoker commandInvoker;
    private static VictoryCondition victoryCondition;
    private static ChessGameLogic chessGameLogic;

    static void initializeGameComponents() {
        chessGameState = new ChessGameState();
        commandInvoker = new CommandInvoker();
        commandInvoker.addUndoRedoListener(ChessGameManager::updateUI);
        DrawCondition drawCondition = new DrawCondition();
        victoryCondition = new VictoryCondition();
        chessGameTurn = new ChessGameTurn(drawCondition, victoryCondition);

        chessBoardUI = createChessBoardUI(chessGameState);
        chessGameLogic = createChessGameLogic(chessGameTurn, commandInvoker, chessBoardUI, chessGameState);

        setupGameLogic(chessGameLogic, chessBoardUI, chessGameState, chessGameTurn, drawCondition, victoryCondition);

        GameStateManager.setChessGameTurn(chessGameTurn);
    }

    private static ChessBoardUI createChessBoardUI(ChessGameState chessGameState) {
        return new ChessBoardUI(chessGameState);
    }

    private static ChessGameLogic createChessGameLogic(ChessGameTurn chessGameTurn,
                                                       CommandInvoker commandInvoker,
                                                       ChessBoardUI chessBoardUI,
                                                       ChessGameState chessGameState) {
        CastlingLogic castlingLogic = new CastlingLogic(chessBoardUI);
        PromotionLogic promotionLogic = new PromotionLogic(chessGameState, chessBoardUI);
        ChessGameLogic chessGameLogic = new ChessGameLogic(chessGameTurn, commandInvoker, castlingLogic, promotionLogic);
        chessGameLogic.setGameEventListener(chessBoardUI, chessGameState);
        castlingLogic.setCastlingLogic(chessGameState, chessGameLogic);
        return chessGameLogic;
    }

    private static void setupGameLogic(ChessGameLogic chessGameLogic, ChessBoardUI chessBoardUI, ChessGameState chessGameState, ChessGameTurn chessGameTurn, DrawCondition drawCondition, VictoryCondition victoryCondition) {
        chessBoardUI.setGameLogicActions(chessGameLogic);
        chessGameTurn.setChessGameState(chessGameState);
        chessGameState.setGameLogicActions(chessGameLogic);
        victoryCondition.setVictoryCondition(chessGameState, chessGameTurn);
        drawCondition.setDrawCondition(chessGameState, chessGameLogic, chessGameTurn);
    }

    public static void updateUI() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                JPanel panel = chessBoardUI.getPanelAtPosition(new Position(x, y));

                panel.removeAll();
                chessBoardUI.setDefaultTileBackground(y * 8 + x, panel);
            }
        }

        for (Map.Entry<Position, ChessPiece> entry : chessGameState.getChessPieces().entrySet()) {
            chessBoardUI.placePieceOnboard(entry.getKey(), entry.getValue());

        }
    }
}
