package hello;

import hello.gameobject.ChessBoard;
import hello.gameobject.ChessPiece;

public class PieceSelectionManager {
    private final ChessBoard chessBoard;

    public PieceSelectionManager(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    boolean isSelectable(ChessPiece piece) {
        return chessBoard.getCurrentPlayerColor() == piece.getColor();
    }

    public void selectPiece(ChessPiece piece) {
        if (isSelectable(piece)) {
            if(chessBoard.getSelectedPiece() == piece){
                chessBoard.setSelectedPiece(null);
                chessBoard.getUIManager().clearHighlights( chessBoard);
            }
            else{
                chessBoard.setSelectedPiece(piece);
                chessBoard.highlightPossibleMoves(chessBoard.getSelectedPiece());
            }

        }
    }
}