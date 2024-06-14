package game.computer;

import game.GameUtils;
import game.Position;
import game.core.GameTurnListener;
import game.core.PlayerManager;
import game.core.factory.ChessPiece;
import game.model.GameLogicActions;
import game.model.state.ChessPieceManager;
import game.util.Color;
import game.util.PieceType;
import lombok.Getter;
import lombok.Setter;

public class ComputerPlayer implements Computer {
    private GameTurnListener gameTurnListener;
    private PlayerManager player;
    private GameLogicActions gameLogicActions;
    private ChessPieceManager chessPieceManager;
    private final Stockfish stockfish;
    @Getter
    private boolean computerSetup = false;
    @Getter
    @Setter
    private boolean active = false;
    private static final int MAX_RETRIES = 10;
    private static final long RETRY_DELAY_MS = 1000; // 1초

    public ComputerPlayer(Stockfish stockfish) {
        this.stockfish = stockfish;
    }

    public void setComputer(ChessPieceManager chessPieceManager,
                            GameLogicActions gameLogicActions,
                            GameTurnListener gameTurnListener,
                            PlayerManager playerManager
    ) {
        this.chessPieceManager = chessPieceManager;
        this.gameLogicActions = gameLogicActions;
        this.gameTurnListener = gameTurnListener;
        this.player = playerManager;
        computerSetup = true;
        stockfish.startEngine();
    }

    @Override
    public void play() {
        if (player.getCurrentPlayerColor() == Color.WHITE) {
            throw new IllegalArgumentException("It is not computer's turn");
        }
        String result = gameTurnListener.computerSerializeGameState();
        System.out.println(result);
        sendMove(result);
    }

    public void sendMove(String boardState) {
        if (boardState == null || boardState.trim().isEmpty()) {
            throw new IllegalArgumentException("Board state cannot be null or empty");
        }
        stockfish.sendCommand("position fen " + boardState);
        stockfish.sendCommand("go movetime 80");
        getResult();

    }

    private void getResult() {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                String output = stockfish.getOutput(80);
                String move = output.split("bestmove ")[1].split(" ")[0];
                differentWithCurrentStatus(move);
                return;
            } catch (Exception e ){
                retries++;
                if (retries == MAX_RETRIES) {
                    System.err.println("최대 재시도 횟수 초과: " + e.getMessage());
                    return; // 최대 재시도 횟수 초과 시 메서드 종료
                }
                System.err.println("재시도 " + retries + "/" + MAX_RETRIES);
                try {
                    Thread.sleep(RETRY_DELAY_MS); // 재시도 전 일정 시간 대기
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void differentWithCurrentStatus(String move) {
        int fromFile = move.charAt(0) - 'a';
        int fromRank = 8 - (move.charAt(1) - '0');
        Position fromPosition = new Position(fromFile, fromRank);

        int toFile = move.charAt(2) - 'a';
        int toRank = 8 - (move.charAt(3) - '0');
        Position toPosition = new Position(toFile, toRank);


        String promotion = move.trim();
        // 프로모션 처리 (예: e7e8q)
        if (promotion.length() > 4) {
            char promotedPiece = move.charAt(4);
            switch (promotedPiece) {
                case 'q':
                    chessPieceManager.getChessPieces().remove(fromPosition);
                    chessPieceManager.getChessPieces().put(fromPosition, new ChessPiece(PieceType.QUEEN, fromPosition, player.getCurrentPlayerColor()));
                    break;
                case 'r':
                    chessPieceManager.getChessPieces().remove(fromPosition);
                    chessPieceManager.getChessPieces().put(fromPosition, new ChessPiece(PieceType.ROOK, fromPosition, player.getCurrentPlayerColor()));
                    break;
                case 'b':
                    chessPieceManager.getChessPieces().remove(fromPosition);
                    chessPieceManager.getChessPieces().put(fromPosition, new ChessPiece(PieceType.BISHOP, fromPosition, player.getCurrentPlayerColor()));
                    break;
                case 'n':
                    chessPieceManager.getChessPieces().remove(fromPosition);
                    chessPieceManager.getChessPieces().put(fromPosition, new ChessPiece(PieceType.KNIGHT, fromPosition, player.getCurrentPlayerColor()));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid promoted piece: " + promotedPiece);
            }
        }
        ChessPiece pieceAtPosition = GameUtils.findPieceAtPosition(chessPieceManager, fromPosition).orElseThrow(
                () -> new IllegalArgumentException("No piece found at position: " + fromPosition)
        );

        gameLogicActions.moveActions(pieceAtPosition, toPosition);

    }


    public void stopEngine() {
        stockfish.stopEngine();
    }


}
