package game.object.castling;

import game.GameUtils;
import game.Position;
import game.command.CommandInvoker;
import game.core.ChessGameTurn;
import game.factory.ChessPiece;
import game.object.GameEventListener;
import game.object.GameStatusListener;

import java.util.Optional;

public class CastlingHandler {
    private final GameStatusListener gameStatusListener;
    private final GameEventListener gameEventListener;
    private final ChessGameTurn gameTurnListener;
    private final CommandInvoker commandInvoker;

    public CastlingHandler(GameStatusListener gameStatusListener, GameEventListener gameEventListener, ChessGameTurn gameTurnListener, CommandInvoker commandInvoker) {
        this.gameStatusListener = gameStatusListener;
        this.gameEventListener = gameEventListener;
        this.gameTurnListener = gameTurnListener;
        this.commandInvoker = commandInvoker;
    }

    public void handleCastlingMove(ChessPiece king, boolean isQueenSide) {
        if (!isQueenSide) {
            moveRookForCastling(new Position(7, king.getPosition().y()), new Position(5, king.getPosition().y()));
        } else {
            moveRookForCastling(new Position(0, king.getPosition().y()), new Position(3, king.getPosition().y()));
        }
    }

    private void moveRookForCastling(Position from, Position to) {
       ChessPiece rook = gameStatusListener.getChessPieceAt(from);
        if (rook != null) {
            gameEventListener.onPieceMoved(to, rook);
            commandInvoker.executeCommand(rook, from, to, gameStatusListener, gameTurnListener);
        }
    }
}
