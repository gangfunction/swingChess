package game.command;

import game.Position;
import game.core.ChessGameTurn;
import game.model.state.CapturedPieceManager;
import game.model.state.ChessPieceManager;
import game.model.state.MoveManager;
import game.util.PieceType;
import game.core.factory.ChessPiece;
import game.model.state.SpecialMoveManager;

import javax.swing.*;

import static game.app.ChessGameManager.chessBoardUI;


public class MoveCommand implements Command {
    private ChessPiece piece;
    private Position startPosition;
    private Position endPosition;
    private SpecialMoveManager specialMoveManager;
    private ChessGameTurn chessGameTurn;
    private CapturedPieceManager capturedPieceManager;
    private MoveManager moveManager;
    private ChessPieceManager chessPieceManager;

    /**
     * Constructor for MoveCommand.
     *
     * @param piece          the chess piece to move
     * @param startPosition  the starting position of the piece
     * @param endPosition    the ending position of the piece
     * @param chessGameState the current state of the chess game
     */
    public MoveCommand(ChessPiece piece,
                       Position startPosition,
                       Position endPosition,
                       SpecialMoveManager chessGameState,
                       ChessGameTurn chessGameTurn,
                       CapturedPieceManager capturedPieceManager,
                       ChessPieceManager chessPieceManager,
                       MoveManager moveManager
    ) {
        this.piece = piece;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.specialMoveManager = chessGameState;
        this.chessGameTurn = chessGameTurn;
        this.capturedPieceManager = capturedPieceManager;
        this.chessPieceManager = chessPieceManager;
        this.moveManager = moveManager;
    }

    public void reset(ChessPiece piece,
                      Position startPosition,
                      Position endPosition,
                      SpecialMoveManager chessGameState,
                      ChessGameTurn chessGameTurn,
                      CapturedPieceManager capturedPieceManager,
                      ChessPieceManager chessPieceManager,
                      MoveManager moveManager
    ) {
        this.piece = piece;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.specialMoveManager = chessGameState;
        this.chessGameTurn = chessGameTurn;
        this.capturedPieceManager = capturedPieceManager;
        this.chessPieceManager = chessPieceManager;
        this.moveManager = moveManager;
    }

    /**
     * Executes the move command.
     */
    @Override
    public void execute() {
        // 시작 위치에서 기물 제거
        chessPieceManager.getChessPieces().remove(startPosition);

        // 기물의 위치를 새로운 위치로 업데이트
        piece.setPosition(endPosition);
        ChessPiece capturedPiece = chessPieceManager.getChessPieces().put(endPosition, piece);
        if (capturedPiece != null) {
            capturedPieceManager.getCapturedPieces().push(capturedPiece);
        }


        // 폰의 더블 무브 처리
        if (piece.getType() == PieceType.PAWN && Math.abs(startPosition.y() - endPosition.y()) == 2) {
            moveManager.updateLastMovedPawn(piece, startPosition, endPosition);
        }

        // UI 업데이트
        updateUI(startPosition, endPosition);
    }

    /**
     * Undoes the move command.
     */
    @Override
    public void undo() {
        // 기물의 위치를 원래 위치로 되돌림
        chessPieceManager.getChessPieces().remove(endPosition);
        chessPieceManager.getChessPieces().put(startPosition, piece);
        piece.setPosition(startPosition);

        // 잡힌 기물이 있을 경우, 해당 기물을 원래 위치에 복원
        if (!capturedPieceManager.getCapturedPieces().isEmpty()) {
            ChessPiece chessPiece = capturedPieceManager.getCapturedPieces().pop();
            if (chessPiece != null) {
                chessPieceManager.getChessPieces().put(endPosition, chessPiece);
            }
        }

        updateUI(endPosition, startPosition);
        chessGameTurn.previousTurn(); // 턴을 이전으로 되돌림
    }

    private void updateUI(Position oldPos, Position newPos) {
        JPanel oldPanel = chessBoardUI.getPanelAtPosition(oldPos);
        JPanel newPanel = chessBoardUI.getPanelAtPosition(newPos);
        oldPanel.revalidate();
        oldPanel.repaint();
        newPanel.revalidate();
        newPanel.repaint();
    }

    /**
     * Redoes the move command.
     */
    @Override
    public void redo() {
        execute();
    }
}