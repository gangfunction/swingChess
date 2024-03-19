package game.command;

import game.GameUtils;
import game.Position;
import game.object.ChessGameState;
import game.factory.ChessPiece;
import game.factory.Type;

public class MoveCommand implements Command {
    private final ChessPiece piece;
    private final Position startPosition;
    private final Position endPosition;
    private final ChessPiece capturedPiece;
    private final ChessGameState chessGameState;
    private final GameUtils gameUtils;



    public MoveCommand(ChessPiece piece, Position startPosition, Position endPosition, ChessGameState chessGameState, GameUtils gameUtils) {
        this.piece = piece;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.chessGameState = chessGameState;
        this.gameUtils = gameUtils;
        this.capturedPiece = findPieceAtEndPosition();
    }
    private ChessPiece findPieceAtEndPosition() {
        return gameUtils.findPieceAtPosition(endPosition.x(), endPosition.y(), chessGameState).orElse(null);
    }

    @Override
    public void execute() {
        piece.setPosition(endPosition);
        chessGameState.setSelectedPiece(null);

        if (piece.getType() == Type.PAWN && Math.abs(startPosition.y() - endPosition.y()) == 2) {
            chessGameState.updateLastMovedPawn(piece, startPosition, endPosition);
        }
    }

    @Override
    public void undo() {
        piece.setPosition(startPosition);

        if (capturedPiece != null) {
            chessGameState.getChessPieces().add(capturedPiece);
        }
    }

    @Override
    public void redo() {
        execute();

    }

}
