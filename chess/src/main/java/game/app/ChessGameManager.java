package game.app;

import game.Position;
import game.command.CommandInvoker;
import game.core.ChessGameTurn;
import game.core.PlayerManager;
import game.core.factory.ChessPiece;
import game.ui.ChessBoardUI;
import game.model.ChessGameLogic;
import game.model.ChessGameState;
import game.model.PromotionLogic;
import game.model.castling.CastlingLogic;
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

    static void initializeGameComponents(PlayerManager playerManager) {
        chessGameState = new ChessGameState();
        commandInvoker = new CommandInvoker();
        commandInvoker.addUndoRedoListener(ChessGameManager::updateUI);
        DrawCondition drawCondition = new DrawCondition();
        victoryCondition = new VictoryCondition();
        chessGameTurn = new ChessGameTurn(drawCondition, victoryCondition);

        chessBoardUI = createChessBoardUI(chessGameState);
        chessGameLogic = createChessGameLogic(chessGameTurn, commandInvoker, chessBoardUI, chessGameState, playerManager);

        setupGameLogic(chessGameLogic, chessBoardUI, chessGameState, chessGameTurn, drawCondition, victoryCondition);

        GameStateManager.setChessGameTurn(chessGameTurn);
    }

    private static ChessBoardUI createChessBoardUI(ChessGameState chessGameState) {
        return new ChessBoardUI(chessGameState);
    }

    private static ChessGameLogic createChessGameLogic(ChessGameTurn chessGameTurn,
                                                       CommandInvoker commandInvoker,
                                                       ChessBoardUI chessBoardUI,
                                                       ChessGameState chessGameState,
                                                       PlayerManager playerManager
    ) {
        CastlingLogic castlingLogic = new CastlingLogic(chessBoardUI);
        PromotionLogic promotionLogic = new PromotionLogic(chessGameState, chessBoardUI);
        ChessGameLogic chessGameLogic = new ChessGameLogic(chessGameTurn,
                commandInvoker,
                castlingLogic,
                promotionLogic,
                playerManager
        );
        chessGameLogic.setGameEventListener(chessBoardUI, chessGameState, chessGameState);
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
