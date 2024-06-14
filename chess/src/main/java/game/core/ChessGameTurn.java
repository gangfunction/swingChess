package game.core;

import game.GameUtils;
import game.Position;
import game.app.ChessGameManager;
import game.computer.ComputerPlayer;
import game.core.factory.ChessPiece;
import game.model.state.ChessPieceManager;
import game.util.PieceType;
import game.model.state.SpecialMoveManager;
import game.observer.ChessObserver;
import game.observer.Observer;
import game.status.DrawCondition;
import game.status.GameStatus;
import game.status.VictoryCondition;
import game.util.Color;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static game.app.ChessGameManager.chessBoardUI;
import static game.app.ChessGameManager.updateUI;

@Slf4j
public class ChessGameTurn implements GameTurnListener, Serializable ,ObserverListener{
    @Serial
    private static final long serialVersionUID = 1L;
    @Setter
    private SpecialMoveManager specialMoveManager;

    private final PlayerManager playerManager;
    private final List<Observer> observers;
    private static final int NUMBER_OF_PLAYERS = 2;
    private int currentPlayerIndex;
    private boolean gameEnded;
    private final DrawCondition drawCondition;
    private final VictoryCondition victoryCondition;
    private final ChessObserver chessObserver;
    private final ChessPieceManager chessPieceManager;
    private final ComputerPlayer computerPlayer;


    public ChessGameTurn(DrawCondition drawCondition,
                         VictoryCondition victoryCondition,
                         ChessPieceManager chessPieceManager,
                         PlayerManager playerManager, ComputerPlayer computerPlayer
    ) {
        this.drawCondition = drawCondition;
        this.victoryCondition = victoryCondition;
        this.chessPieceManager = chessPieceManager;
        this.playerManager = playerManager;
        this.computerPlayer = computerPlayer;
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
        playerManager.nextPlayer();
        Player player = playerManager.getCurrentPlayer();
        notifyObservers(player.getName() + "님의 차례입니다.");
        checkGameStatusAndConditions(player);
        victoryCondition.invalidateKingInCheckCache();
        if (playerManager.getCurrentPlayerColor() == Color.BLACK &&
                computerPlayer.isActive()) {
            computerPlayer.play();
        }
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
                if (victoryCondition.isKingInCheck(chessPieceManager.getKing(playerManager.getCurrentPlayerColor()))) {
                    notifyObservers("체크 " + player.getName() + "님!");
                }
                break;
        }
    }

    private GameStatus determineGameStatus() {
        Color currentPlayerColor = playerManager.getCurrentPlayerColor();
        if (victoryCondition.isCheckMate(playerManager)) {
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
        return getFEN();
    }

    @Override
    public String computerSerializeGameState() {
        return getComputerFEN();
    }

    private String getComputerFEN() {
        Color currentPlayerColor = playerManager.getCurrentPlayerColor();
        return getComputerPiecePlacement() + " " +
                (currentPlayerColor == Color.WHITE ? "w" : "b") + " " + // 현재 플레이어 색상에 따라 'w' 또는 'b' 설정
                getCastlingRights() + " " +
                getEnPassantTarget() + " " +
                specialMoveManager.getHalfMoveClock() + " " +
                getTotalMoves();
    }

    private String getComputerPiecePlacement() {
        StringBuilder placement = new StringBuilder();
        for (int rank = 0; rank < 8; rank++) {
            int emptySquares = 0;
            for (int file = 0; file <8 ; file++) { // 파일 순서를 반대로
                Position position = new Position(file, rank);
                ChessPiece piece = GameUtils.findPieceAtPosition(chessPieceManager, position).orElse(null);
                if (piece == null) {
                    emptySquares++;
                } else {
                    if (emptySquares > 0) {
                        placement.append(emptySquares);
                        emptySquares = 0;
                    }
                    placement.append(piece.getType().getFenSymbol(piece.getColor()));
                }
            }
            if (emptySquares > 0) {
                placement.append(emptySquares);
            }
            if (rank < 7) {
                placement.append("/"); // 마지막 행에서는 "/" 추가 안함
            }
        }
        return placement.toString();
    }
    public String getFEN() {
        Color currentPlayerColor = playerManager.getCurrentPlayerColor();
        return getPiecePlacement() + " " +
                (currentPlayerColor == Color.WHITE ? "w" : "b") + " " + // 현재 플레이어 색상에 따라 'w' 또는 'b' 설정
                getCastlingRights() + " " +
                getEnPassantTarget() + " " +
                specialMoveManager.getHalfMoveClock() + " " +
                getTotalMoves();
    }

    private String getPiecePlacement() {
        StringBuilder placement = new StringBuilder();
        for (int rank = 7; rank >= 0; rank--) {
            int emptySquares = 0;
            for (int file = 0; file < 8; file++) {
                Position position = new Position(file, rank);
                ChessPiece piece = GameUtils.findPieceAtPosition(chessPieceManager, position).orElse(null);
                if (piece == null) {
                    emptySquares++;
                } else {
                    if (emptySquares > 0) {
                        placement.append(emptySquares);
                        emptySquares = 0;
                    }
                    placement.append(piece.getType().getFenSymbol(piece.getColor()));
                }
            }
            if (emptySquares > 0) {
                placement.append(emptySquares);
            }
            if (rank > 0) {
                placement.append("/");
            }
        }
        return placement.toString();
    }
    private String getCastlingRights() {
        StringBuilder rights = new StringBuilder();
        if (specialMoveManager.isKingSideCastlingAllowed(Color.WHITE)) {
            rights.append("K");
        }
        if (specialMoveManager.isQueenSideCastlingAllowed(Color.WHITE)) {
            rights.append("Q");
        }
        if (specialMoveManager.isKingSideCastlingAllowed(Color.BLACK)) {
            rights.append("k");
        }
        if (specialMoveManager.isQueenSideCastlingAllowed(Color.BLACK)) {
            rights.append("q");
        }
        return rights.isEmpty() ? "-" : rights.toString();
    }

    private String getEnPassantTarget() {
        ChessPiece lastMovedPawn = specialMoveManager.getLastMovedPiece();
        if (lastMovedPawn == null || !lastMovedPawn.getType().equals(PieceType.PAWN)) {
            return "-"; // 앙파상 타겟이 없는 경우 "-" 반환
        }

        boolean lastMoveWasDoubleStep = specialMoveManager.getLastMoveWasDoubleStep();
        if (!lastMoveWasDoubleStep) {
            return "-"; // 마지막 이동이 2칸 이동이 아닌 경우 "-" 반환
        }

        Position lastMovedPosition = lastMovedPawn.getPosition();
        Color color = lastMovedPawn.getColor();
        int direction = getMoveDirection(color);
        int targetX = lastMovedPosition.x();
        int targetY = lastMovedPosition.y() + (-direction);

        // 앙파상 타겟 위치를 FEN 표기법에 맞게 변환
        return String.format("%c%d", (char)('a' + targetX), 8 - targetY);
    }

    private int getMoveDirection(Color color) {
        return color == Color.WHITE ? -1 : 1;
    }

    private int getTotalMoves() {
        return (specialMoveManager.getHalfMoveClock() + 1) / 2 + 1;
    }

    @Override
    public void deserializeGameState(String gameState) {
        specialMoveManager.clearBoard();
        chessBoardUI.clearHighlights();
        String[] parts = gameState.split(" ");

        // 체스 말 배치 정보 처리
        String piecePlacement = parts[0];
        String[] rows = piecePlacement.split("/");
        for (int rank = 7; rank >= 0; rank--) {
            String row = rows[7 - rank];
            int file = 0;
            for (int i = 0; i < row.length(); i++) {
                char c = row.charAt(i);
                if (Character.isDigit(c)) {
                    file += Character.getNumericValue(c);
                } else {
                    PieceType type = PieceType.fromFenSymbol(c);
                    Color color = (Character.isUpperCase(c)) ? Color.WHITE : Color.BLACK;
                    Position position = new Position(file, rank);
                    ChessPiece piece = new ChessPiece(type, position, color);
                    chessPieceManager.getChessPieces().put(position, piece);
                    file++;
                }
            }
        }

        Color currentPlayerColor;
        if (parts[1].equals("w")) {
            currentPlayerColor = Color.WHITE;
        } else {
            currentPlayerColor = Color.BLACK;
        }
        for (int i = 0; i < playerManager.getPlayers().size(); i++) {
            if (playerManager.getPlayers().get(i).equals(currentPlayerColor)) {
                currentPlayerIndex = i;
                break;
            }
        }

        // 캐슬링 권한 처리
        specialMoveManager.setCastlingRights(parts[2]);

        // 앙파상 타겟 처리
        if (!parts[3].equals("-")) {
            int file = parts[3].charAt(0) - 'a';
            int rank = 8 - Integer.parseInt(parts[3].substring(1));
            specialMoveManager.setEnPassantTarget(new Position(file, rank));
        }

        // 50 move rule 처리
        specialMoveManager.setHalfMoveClock(Integer.parseInt(parts[4]));


        //getChessPieces 메소드를 사용하여 체스 말 배치 정보를 가져온 후 UI에 반영
        updateUI();

        notifyObservers("Game state deserialized and UI updated.");
        chessObserver.setGameState(gameState);
    }

}
