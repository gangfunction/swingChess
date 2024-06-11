package game.model.castling;

import game.Position;
import game.command.CommandInvoker;
import game.core.ChessGameTurn;
import game.core.factory.ChessPiece;
import game.model.state.CapturedPieceManager;
import game.model.state.ChessPieceManager;
import game.model.state.MoveManager;
import game.ui.GameEventListener;
import game.model.state.SpecialMoveManager;

public class CastlingHandler {
    private final SpecialMoveManager specialMoveManager;
    private final GameEventListener gameEventListener;
    private final ChessGameTurn gameTurnListener;
    private final CommandInvoker commandInvoker;
    private final CapturedPieceManager capturedPieceManager;
    private final ChessPieceManager chessPieceManager;
    private final MoveManager moveManager;

    public CastlingHandler(SpecialMoveManager specialMoveManager,
                           GameEventListener gameEventListener,
                           ChessGameTurn gameTurnListener,
                           CommandInvoker commandInvoker,
                           CapturedPieceManager capturedPieceManager,
                           ChessPieceManager chessPieceManager, MoveManager moveManager
    ) {
        this.specialMoveManager = specialMoveManager;
        this.gameEventListener = gameEventListener;
        this.gameTurnListener = gameTurnListener;
        this.commandInvoker = commandInvoker;
        this.capturedPieceManager = capturedPieceManager;
        this.chessPieceManager = chessPieceManager;
        this.moveManager = moveManager;
    }

    public void handleCastlingMove(ChessPiece king, boolean isQueenSide) {
        if (!isQueenSide) {
            moveRookForCastling(new Position(7, king.getPosition().y()), new Position(5, king.getPosition().y()));
        } else {
            moveRookForCastling(new Position(0, king.getPosition().y()), new Position(3, king.getPosition().y()));
        }
    }

    private void moveRookForCastling(Position from, Position to) {
       ChessPiece rook = chessPieceManager.getChessPieceAt(from);
        if (rook != null) {
            gameEventListener.onPieceMoved(to, rook);
            commandInvoker.executeCommand(rook, from, to,
                    specialMoveManager,
                    gameTurnListener,
                    capturedPieceManager,
                    chessPieceManager,
                    moveManager);
        }
    }
}
