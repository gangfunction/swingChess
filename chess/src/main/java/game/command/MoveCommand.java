package game.command;

import game.Position;
import game.core.ChessGameTurn;
import game.factory.PieceType;
import game.factory.ChessPiece;
import game.object.GameStatusListener;

import javax.swing.*;

import static game.app.ChessGameManager.chessBoardUI;


public class MoveCommand implements Command {
    private ChessPiece piece;
    private Position startPosition;
    private Position endPosition;
    private GameStatusListener chessGameState;
    private ChessGameTurn chessGameTurn;

    /**
     * Constructor for MoveCommand.
     *
     * @param piece           the chess piece to move
     * @param startPosition   the starting position of the piece
     * @param endPosition     the ending position of the piece
     * @param chessGameState  the current state of the chess game
     */
    public MoveCommand(ChessPiece piece, Position startPosition, Position endPosition, GameStatusListener chessGameState, ChessGameTurn chessGameTurn) {
        this.piece = piece;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.chessGameState = chessGameState;
        this.chessGameTurn = chessGameTurn;
    }
    public void reset(ChessPiece piece, Position startPosition, Position endPosition, GameStatusListener chessGameState, ChessGameTurn chessGameTurn) {
        this.piece = piece;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.chessGameState = chessGameState;
        this.chessGameTurn = chessGameTurn;
    }

    /**
     * Executes the move command.
     */
    @Override
    public void execute() {
        // 시작 위치에서 기물 제거
        chessGameState.getChessPieces().remove(startPosition);

        // 기물의 위치를 새로운 위치로 업데이트
        piece.setPosition(endPosition);
        ChessPiece capturedPiece = chessGameState.getChessPieces().put(endPosition, piece);
        if (capturedPiece != null) {
            chessGameState.getCapturedPieces().push(capturedPiece);
        }


        // 폰의 더블 무브 처리
        if (piece.getType() == PieceType.PAWN && Math.abs(startPosition.y() - endPosition.y()) == 2) {
            chessGameState.updateLastMovedPawn(piece, startPosition, endPosition);
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
        chessGameState.getChessPieces().remove(endPosition);
        chessGameState.getChessPieces().put(startPosition, piece);
        piece.setPosition(startPosition);

        // 잡힌 기물이 있을 경우, 해당 기물을 원래 위치에 복원
        if(!chessGameState.getCapturedPieces().isEmpty()){
            ChessPiece chessPiece = chessGameState.getCapturedPieces().pop();
            if (chessPiece != null) {
                chessGameState.getChessPieces().put(endPosition, chessPiece);
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