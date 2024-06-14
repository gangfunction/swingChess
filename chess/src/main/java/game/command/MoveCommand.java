package game.command;

import game.Position;
import game.model.state.CapturedPieceManager;
import game.model.state.ChessPieceManager;
import game.model.state.MoveManager;
import game.util.PieceType;
import game.core.factory.ChessPiece;

import javax.swing.*;

import static game.app.ChessGameManager.chessBoardUI;


public class MoveCommand implements Command {
    private ChessPiece piece;
    private Position startPosition;
    private Position endPosition;
    private CapturedPieceManager capturedPieceManager;
    private MoveManager moveManager;
    private ChessPieceManager chessPieceManager;

    public MoveCommand(ChessPiece piece,
                       Position startPosition,
                       Position endPosition,
                       CapturedPieceManager capturedPieceManager,
                       ChessPieceManager chessPieceManager,
                       MoveManager moveManager
    ) {
        this.piece = piece;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.capturedPieceManager = capturedPieceManager;
        this.chessPieceManager = chessPieceManager;
        this.moveManager = moveManager;
    }

    public void reset(ChessPiece piece,
                      Position startPosition,
                      Position endPosition,
                      CapturedPieceManager capturedPieceManager,
                      ChessPieceManager chessPieceManager,
                      MoveManager moveManager
    ) {
        this.piece = piece;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.capturedPieceManager = capturedPieceManager;
        this.chessPieceManager = chessPieceManager;
        this.moveManager = moveManager;
    }

    @Override
    public void execute() {
        chessPieceManager.getChessPieces().remove(startPosition);

        piece.setPosition(endPosition);
        ChessPiece capturedPiece = chessPieceManager.getChessPieces().put(endPosition, piece);
        if (capturedPiece != null) {
            capturedPieceManager.getCapturedPieces().push(capturedPiece);
        }


        if (piece.getType() == PieceType.PAWN && Math.abs(startPosition.y() - endPosition.y()) == 2) {
            moveManager.updateLastMovedPawn(piece, startPosition, endPosition);
        }

        updateUI(startPosition, endPosition);
    }

    @Override
    public void undo() {
        chessPieceManager.getChessPieces().remove(endPosition);
        chessPieceManager.getChessPieces().put(startPosition, piece);
        piece.setPosition(startPosition);

        if (!capturedPieceManager.getCapturedPieces().isEmpty()) {
            ChessPiece chessPiece = capturedPieceManager.getCapturedPieces().pop();
            if (chessPiece != null) {
                chessPieceManager.getChessPieces().put(endPosition, chessPiece);
            }
        }

        updateUI(endPosition, startPosition);
    }

    private void updateUI(Position oldPos, Position newPos) {
        JPanel oldPanel = chessBoardUI.getPanelAtPosition(oldPos);
        JPanel newPanel = chessBoardUI.getPanelAtPosition(newPos);
        oldPanel.revalidate();
        oldPanel.repaint();
        newPanel.revalidate();
        newPanel.repaint();
    }

    @Override
    public void redo() {
        execute();
    }
}