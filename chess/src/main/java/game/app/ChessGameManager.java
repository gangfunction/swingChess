package game.app;

import game.Position;
import game.command.CommandInvoker;
import game.computer.ComputerPlayer;
import game.core.ChessGameTurn;
import game.core.PlayerManager;
import game.core.factory.ChessPiece;
import game.ui.ChessBoardUI;
import game.model.ChessGameLogic;
import game.model.state.ChessGameState;
import game.model.PromotionLogic;
import game.model.castling.CastlingLogic;
import game.status.DrawCondition;
import game.status.VictoryCondition;

import javax.swing.*;
import java.util.Map;

public class ChessGameManager {
    public static  ChessBoardUI chessBoardUI;
    private static ChessGameState chessGameState;
    static ChessGameTurn chessGameTurn;
    static CommandInvoker commandInvoker;

    static void initializeGameComponents(PlayerManager playerManager,ComputerPlayer computerPlayer){
        chessGameState = new ChessGameState();
        commandInvoker = new CommandInvoker();
        commandInvoker.addUndoRedoListener(ChessGameManager::updateUI);
        DrawCondition drawCondition = new DrawCondition();
        VictoryCondition victoryCondition = new VictoryCondition();
        chessGameTurn = new ChessGameTurn(drawCondition, victoryCondition, chessGameState,playerManager,computerPlayer);

        chessBoardUI = createChessBoardUI(chessGameState);

        ChessGameLogic chessGameLogic = createChessGameLogic(chessGameTurn,
                commandInvoker,
                chessBoardUI,
                chessGameState,
                playerManager,
                computerPlayer
        );

        setupGameLogic(chessGameLogic,
                chessBoardUI, chessGameState,
                chessGameTurn, drawCondition,
                victoryCondition, playerManager, computerPlayer);

        GameStateManager.setChessGameTurn(chessGameTurn);
    }

    private static ChessBoardUI createChessBoardUI(ChessGameState chessGameState) {
        return new ChessBoardUI(chessGameState, chessGameState);
    }

    private static ChessGameLogic createChessGameLogic(ChessGameTurn chessGameTurn,
                                                       CommandInvoker commandInvoker,
                                                       ChessBoardUI chessBoardUI,
                                                       ChessGameState chessGameState,
                                                       PlayerManager playerManager,
                                                       ComputerPlayer computerPlayer
    ) {
        CastlingLogic castlingLogic = new CastlingLogic(chessBoardUI, chessGameState);
        PromotionLogic promotionLogic = new PromotionLogic(chessGameState, chessBoardUI, chessGameState);
        ChessGameLogic chessGameLogic = new ChessGameLogic(chessGameTurn,
                commandInvoker,
                castlingLogic,
                promotionLogic,
                playerManager,
                chessGameState,
                chessGameState
        );
        chessGameLogic.setGameEventListener(chessBoardUI, chessGameState, chessGameState, chessGameState);
        castlingLogic.setCastlingLogic(chessGameState, chessGameLogic);
        return chessGameLogic;
    }

    private static void setupGameLogic(ChessGameLogic chessGameLogic,
                                       ChessBoardUI chessBoardUI,
                                       ChessGameState chessGameState,
                                       ChessGameTurn chessGameTurn,
                                       DrawCondition drawCondition,
                                       VictoryCondition victoryCondition,
                                        PlayerManager playerManager,
                                        ComputerPlayer computerPlayer
    ) {
        chessBoardUI.setGameLogicActions(chessGameLogic);
        chessGameTurn.setSpecialMoveManager(chessGameState);
        victoryCondition.setVictoryCondition(chessGameState, chessGameTurn, chessGameState, chessGameState);
        drawCondition.setDrawCondition(chessGameState, chessGameLogic, chessGameTurn, chessGameState);
        computerPlayer.setComputer(chessGameState, chessGameLogic, chessGameTurn, playerManager);
    }

    public static void updateUI() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                JPanel panel = chessBoardUI.getPanelAtPosition(new Position(x, y));

                panel.removeAll();
                panel.revalidate();
                panel.repaint();

                chessBoardUI.setDefaultTileBackground(y * 8 + x, panel);
            }
        }

        for (Map.Entry<Position, ChessPiece> entry : chessGameState.getChessPieces().entrySet()) {
            chessBoardUI.placePieceOnboard(entry.getKey(), entry.getValue());

        }

    }
}
