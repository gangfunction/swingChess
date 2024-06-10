package game.core;

import game.Position;
import game.core.factory.ChessPiece;
import game.util.PieceType;
import game.model.GameStatusListener;
import game.observer.ChessObserver;
import game.observer.Observer;
import game.status.DrawCondition;
import game.status.GameStatus;
import game.status.VictoryCondition;
import game.ui.IconLoader;
import game.util.Color;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static game.app.ChessGameManager.chessBoardUI;
import static game.app.ChessGameManager.updateUI;

@Slf4j
public class ChessGameTurn implements GameTurnListener, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Setter
    private GameStatusListener chessGameState;

    private final PlayerManager playerManager;
    private final List<Observer> observers;
    private static final int NUMBER_OF_PLAYERS = 2;
    private int currentPlayerIndex;
    private boolean gameEnded;
    private final DrawCondition drawCondition;
    private final VictoryCondition victoryCondition;
    private final ChessObserver chessObserver;

    /**
     * Constructor for ChessGameTurn.
     *
     * @param drawCondition    the condition for a draw
     * @param victoryCondition the condition for victory
     */
    public ChessGameTurn(DrawCondition drawCondition, VictoryCondition victoryCondition) {
        this.drawCondition = drawCondition;
        this.victoryCondition = victoryCondition;
        this.playerManager = new PlayerManager();
        this.currentPlayerIndex = 0;
        this.gameEnded = false;
        this.observers = new ArrayList<>();
        this.chessObserver = new ChessObserver();
    }


    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
        chessObserver.addObserver(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
        chessObserver.setGameState(serializeGameState());
    }

    @Override
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % NUMBER_OF_PLAYERS;
        Player player = playerManager.getCurrentPlayer();
        playerManager.nextPlayer();
        notifyObservers(player.getName() + "님의 차례입니다.");
        checkGameStatusAndConditions(player);
        victoryCondition.invalidateKingInCheckCache();
    }

    private void checkGameStatusAndConditions(Player player) {
        GameStatus gameStatus = determineGameStatus();
        switch (gameStatus) {
            case CHECKMATE:
                JOptionPane.showMessageDialog(null, "체크메이트! " + player.getName() + "님의 승리입니다.");
                endGame();
                break;
            case STALEMATE:
                notifyObservers("스테일메이트! 무승부입니다.");
                endGame();
                break;
            case DRAW:
                notifyObservers("무승부입니다.");
                endGame();
                break;
            case ONGOING:
                if (victoryCondition.isKingInCheck(chessGameState.getKing(PlayerManager.getCurrentPlayerColor()))) {
                    notifyObservers("체크 " + player.getName() + "님!");
                }
                break;
        }
    }

    private GameStatus determineGameStatus() {
        Color currentPlayerColor = PlayerManager.getCurrentPlayerColor();
        if (victoryCondition.isCheckMate()) {
            return GameStatus.CHECKMATE;
        } else if (drawCondition.isStalemate(currentPlayerColor)) {
            return GameStatus.STALEMATE;
        } else if (drawCondition.isDraw()) {
            return GameStatus.DRAW;
        } else {
            return GameStatus.ONGOING;
        }
    }

    @Override
    public boolean isGameEnded() {
        return gameEnded;
    }

    @Override
    public void endGame() {
        this.gameEnded = true;
    }

    @Override
    public String serializeGameState() {
        StringBuilder builder = new StringBuilder();
        List<ChessPiece> pieces = new ArrayList<>(chessGameState.getChessPieces().values());
        for (ChessPiece piece : pieces) {
            builder.append(piece.getType())
                    .append("_")
                    .append(piece.getColor())
                    .append(":")
                    .append(piece.getPosition().x())
                    .append(" ")
                    .append(piece.getPosition().y())
                    .append(";")
                    .append("\n");
        }
        builder.append("TURN:").append(PlayerManager.getCurrentPlayerColor()).append(";").append("\n");
        builder.append("CASTLING:").append(chessGameState.getCastlingRights()).append(";").append("\n");
        Optional.ofNullable(chessGameState.getEnPassantTarget())
                .ifPresent(target -> builder.append("EnPassant:").append(target).append(";").append("\n"));
        builder.append("GameEnded:").append(isGameEnded() ? "Yes" : "No").append(";").append("\n");
        String gameState = builder.toString();
        chessObserver.setGameState(gameState);
        return gameState;
    }
    @Override
    public void deserializeGameState(String gameState) {
            chessGameState.clearBoard();
            chessBoardUI.clearHighlights();

            String[] parts = gameState.split("\n");
            Color currentPlayerColor = null;
            boolean gameEnded = false;

        for (String part : parts) {
                if (part.startsWith("TURN:")) {
                    currentPlayerColor = Color.valueOf(part.split(":")[1].trim().replace(";", ""));
                } else if (part.startsWith("CASTLING:")) {
                } else if (part.startsWith("GameEnded:")) {
                    gameEnded = part.split(":")[1].trim().equalsIgnoreCase("YES");
                } else if (part.startsWith("EnPassant:")) {
                } else {
                    String[] pieceInfo = part.split(":");
                    String[] pieceHeader = pieceInfo[0].split("_");
                    String[] pieceBody = pieceInfo[1].split(" ");
                    PieceType type = PieceType.valueOf(pieceHeader[0].trim().toUpperCase());
                    Color color = Color.valueOf(pieceHeader[1].trim().toUpperCase().replace(";", ""));
                    int x = Integer.parseInt(pieceBody[0]);
                    int y = Integer.parseInt(pieceBody[1].replace(";", ""));
                    System.out.println("Piece: " + type + " " + color + " at " + x + " " + y);
                    chessGameState.getChessPieces().put(new Position(x, y), new ChessPiece(type, new Position(x, y),color));
                }
            }
            this.gameEnded = gameEnded;

            for (int i = 0; i < playerManager.getPlayers().size(); i++) {
                if (playerManager.getPlayers().get(i).equals(currentPlayerColor)) {
                    currentPlayerIndex = i;
                    break;
                }
            }
            for(Map.Entry<Position, ChessPiece> entry : chessGameState.getChessPieces().entrySet()){
                chessGameState.getChessPieces().put(entry.getKey(), entry.getValue());
                JPanel panel = chessBoardUI.getPanelAtPosition(entry.getKey());
                chessBoardUI.addPieceToPanel(panel, new JLabel(IconLoader.loadIcon(entry.getValue().getType(), entry.getValue().getColor())));
            }
            updateUI();
            notifyObservers("Game state deserialized and UI updated.");
            chessObserver.setGameState(gameState);
    }

    public void previousTurn() {
        currentPlayerIndex = (currentPlayerIndex - 1 + NUMBER_OF_PLAYERS) % NUMBER_OF_PLAYERS;
        Player player = playerManager.getCurrentPlayer();
        notifyObservers(player.getName() + "님의 차례입니다.");
    }
}
