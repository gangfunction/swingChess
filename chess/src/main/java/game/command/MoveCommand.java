package game.command;

import game.GameUtils;
import game.Position;
import game.factory.PieceType;
import game.factory.ChessPiece;
import game.object.GameStatusListener;

public class MoveCommand implements Command {
    private final ChessPiece piece;
    private final Position startPosition;
    private final Position endPosition;
    private final ChessPiece capturedPiece;
    private final GameStatusListener chessGameState;
    private final GameUtils gameUtils;

    /**
     * Constructor for MoveCommand.
     *
     * @param piece           the chess piece to move
     * @param startPosition   the starting position of the piece
     * @param endPosition     the ending position of the piece
     * @param chessGameState  the current state of the chess game
     * @param gameUtils       utility class for game-related operations
     */
    public MoveCommand(ChessPiece piece, Position startPosition, Position endPosition, GameStatusListener chessGameState, GameUtils gameUtils) {
        this.piece = piece;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.chessGameState = chessGameState;
        this.gameUtils = gameUtils;
        this.capturedPiece = findPieceAtEndPosition();
    }

    /**
     * Finds the piece at the end position, if any.
     *
     * @return the piece at the end position, or null if no piece is found
     */
    private ChessPiece findPieceAtEndPosition() {
        return gameUtils.findPieceAtPosition(chessGameState, endPosition).orElse(null);
    }

    /**
     * Executes the move command.
     */
    @Override
    public void execute() {
        piece.setPosition(endPosition);

        // Handle special case for pawn double move
        if (piece.getType() == PieceType.PAWN && Math.abs(startPosition.y() - endPosition.y()) == 2) {
            chessGameState.updateLastMovedPawn(piece, startPosition, endPosition);
        }

        // Remove captured piece from the game state
        if (capturedPiece != null) {
            chessGameState.getChessPieces().remove(capturedPiece);
        }
    }

    /**
     * Undoes the move command.
     */
    @Override
    public void undo() {
        piece.setPosition(startPosition);

        // Restore captured piece to the game state
        if (capturedPiece != null) {
            chessGameState.getChessPieces().add(capturedPiece);
        }
    }

    /**
     * Redoes the move command.
     */
    @Override
    public void redo() {
        execute();
    }
}