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
        stockfish.sendCommand("go movetime 500");
        getResult();

    }

    private void getResult() {
        String output = stockfish.getOutput(500);
        System.out.println(output);
        String move = output.split("bestmove ")[1].split(" ")[0];
        System.out.println(move);
        differentWithCurrentStatus(move);
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

        gameLogicActions.executeMove(pieceAtPosition, toPosition);

    }


    public void stopEngine() {
        stockfish.stopEngine();
    }


}
