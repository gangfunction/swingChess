package hello.command;

import hello.gameobject.ChessBoard;
import hello.gameobject.ChessPiece;
import hello.Position;

import java.util.Optional;

public class MoveCommand implements Command {
    private final ChessPiece piece;
    private final Position startPosition;
    private final Position endPosition;
    private final ChessBoard chessBoard;
    private final ChessPiece capturedPiece;
    private int[][] boardCondition;

    public MoveCommand(ChessPiece piece, Position startPosition, Position endPosition, ChessBoard Chessboard) {
        this.piece = piece;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.chessBoard = Chessboard;
        this.capturedPiece = null;
    }

    @Override
    public void execute() {
        Optional<ChessPiece> opponentPiece = chessBoard.getDistanceManager().
                findPieceAtPosition(endPosition.getX(), endPosition.getY(), chessBoard);
        opponentPiece.ifPresent(chessBoard.getChessPieces()::remove);
        chessBoard.clearSquare(startPosition);
        piece.setPosition(endPosition);
        chessBoard.movePiece(chessBoard.getSelectedPiece(), endPosition);
        chessBoard.getUIManager().clearHighlights(chessBoard);
    }

    @Override
    public void undo() {
        chessBoard.clearSquare(endPosition);
        piece.setPosition(startPosition);
        chessBoard.movePiece(piece, startPosition);

        if (capturedPiece != null) {
            chessBoard.getChessPieces().add(capturedPiece);
            chessBoard.addPieceToBoard(capturedPiece);
        }
    }

    @Override
    public void redo() {
        chessBoard.clearSquare(startPosition);
        piece.setPosition(endPosition);
        chessBoard.movePiece(piece, endPosition);

        if (capturedPiece != null) {
            chessBoard.getChessPieces().remove(capturedPiece);
            chessBoard.clearSquare(capturedPiece.getPosition());
        }

    }
}
