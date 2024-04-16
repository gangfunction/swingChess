package game.core;

import game.Position;
import game.factory.ChessPiece;
import game.object.GameStatusListener;
import game.observer.Observer;
import game.status.DrawCondition;
import game.status.VictoryCondition;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ChessGameTurn implements GameTurnListener {
    private GameStatusListener chessGameState;
    private List<Player> players = new ArrayList<>();
    private final List<Observer> observers = new ArrayList<>();
    private static final int NUMBER_OF_PLAYERS = 2; // 플레이어 수
    private int currentPlayerIndex; // 현재 차례인 플레이어의 인덱스
    private boolean gameEnded; // 게임 종료 여부
    private final DrawCondition drawCondition;
    private final VictoryCondition victoryCondition;
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
    public void setChessGameState(GameStatusListener chessGameState) {
        this.chessGameState = chessGameState;
    }

    public ChessGameTurn(DrawCondition drawCondition, VictoryCondition victoryCondition) {
        this.drawCondition = drawCondition;
        this.victoryCondition = victoryCondition;
        this.players=initializePlayers();
        this.currentPlayerIndex = 0;
        this.gameEnded = false;

    }
     List<Player> initializePlayers() {
        players.add(new Player("pin", Color.WHITE));
        players.add(new Player("jake", Color.BLACK));
        return players;
    }

    // 다음 플레이어의 차례로 넘어가는 메서드
    @Override
    public void nextTurn() {

        currentPlayerIndex = (currentPlayerIndex + 1) % NUMBER_OF_PLAYERS; // 리스트의 다음 인덱스로 이동. 플레이어 수를 초과하는 경우 0으로 되돌림
        Player player = getCurrentPlayer();
        notifyObservers(player.getName() + "님의 차례입니다.");

        if(drawCondition.isStalemate(getCurrentPlayerColor())) {
            notifyObservers("스테일메이트! 무승부입니다.");
            endGame();
        }
        else if (victoryCondition.isKingInCheck(chessGameState.getKing(getCurrentPlayerColor()))){
            notifyObservers("체크 " + player.getName() + "님!");
            if (victoryCondition.isCheckMate()) {
                JOptionPane.showMessageDialog(null, "체크메이트! " + player.getName() + "님의 승리입니다.");
                endGame();
            }
        }
    }



    @Override
    public void resetGame() {
        this.gameEnded = false;
        // 추가: 체스판 초기화 및 기타 시작에 필요한 로직 수행
    }
    @Override
    public boolean isGameEnded() {
        return gameEnded;
    }

    @Override
    public void setPlayerTurn(String playerName) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equalsIgnoreCase(playerName)) {
                currentPlayerIndex = i;
                notifyObservers(playerName + "님의 차례입니다.");
                break;
            }
        }
    }

    // 게임 종료 메서드
    @Override
    public void endGame() {
        this.gameEnded = true;
    }

    // 현재 차례인 플레이어를 가져오는 메서드
    @Override
    public Player getCurrentPlayer() {
        Player currentPlayer = players.get(currentPlayerIndex);
        System.out.println("It's " + currentPlayer + "'s turn. (" + currentPlayer.getColor() + ")");

        return currentPlayer;
    }

    @Override
    public Color getCurrentPlayerColor() {
        return getCurrentPlayer().getColor();
    }

    @Override
    public String serializeGameState() {
        StringBuilder builder = new StringBuilder();
        List<ChessPiece> pieces = chessGameState.getChessPieces();
        for (ChessPiece piece : pieces) {
            builder.append(piece.getType())
                    .append(piece.getColor())
                    .append(piece.getPosition().x())
                    .append(piece.getPosition().y())
                    .append(";");
        }
        Color currentPlayerColor = getCurrentPlayerColor();
        builder.append("Turn:").append(currentPlayerColor).append(";");
        builder.append("Castling:").append(chessGameState.getCastlingRights()).append(";");
        Position enPassantTarget = chessGameState.getEnPassantTarget();
        if (enPassantTarget != null) {
            builder.append("EnPassant:").append(enPassantTarget).append(";");
        }
        builder.append("GameEnded:").append(isGameEnded() ? "Yes" : "No").append(";");

        return builder.toString();
    }
}
