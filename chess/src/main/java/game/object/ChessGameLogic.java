package game.object;

import game.GameUtils;
import game.Position;
import game.command.Command;
import game.command.CommandInvoker;
import game.command.MoveCommand;
import game.core.ChessGameTurn;
import game.core.Color;
import game.factory.ChessPiece;
import game.factory.PieceType;
import game.observer.ChessObserver;
import game.status.DrawCondition;
import lombok.Setter;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public class ChessGameLogic  implements GameLogicActions {
    private GameEventListener gameEventListener;
    private GameStatusListener gameStatusListener;

    private final CastlingLogic castlingLogic;
    private final PromotionLogic promotionLogic;
    private final DrawCondition drawCondition ;
    private final GameUtils gameUtils = new GameUtils();
    private final ChessGameTurn chessGameTurn;
    private final CommandInvoker commandInvoker;
    private final ChessObserver chessObserver;
    @Setter
    private boolean afterCastling = false;



    public ChessGameLogic(ChessGameTurn chessGameTurn, CommandInvoker commandInvoker, CastlingLogic castlingLogic, PromotionLogic promotionLogic) {
        this.chessGameTurn = chessGameTurn;
        this.commandInvoker = commandInvoker;
        this.castlingLogic = castlingLogic;
        this.promotionLogic = promotionLogic;
        this.chessObserver = new ChessObserver();
        this.drawCondition = new DrawCondition();
        this.chessObserver.addObserver(new GameUIObserver());
    }

    public void setGameEventListener(GameEventListener gameEventListener,GameStatusListener gameStatusListener) {
        this.gameEventListener = gameEventListener;
        this.gameStatusListener = gameStatusListener;
        this.drawCondition.setDrawCondition(gameStatusListener, this, chessGameTurn);
        this.castlingLogic.setCastlingLogic(gameStatusListener, this);

    }


    public void handleSquareClick(int x, int y) {
        Position clickedPosition = new Position(x, y);
        Optional<ChessPiece> targetPiece = gameUtils.findPieceAtPosition(gameStatusListener, clickedPosition);
        if (targetPiece.isPresent() && gameStatusListener.getSelectedPiece() == null) {
            handlePieceSelection(targetPiece.get(), clickedPosition);
        } else if (gameStatusListener.getSelectedPiece() != null) {
            ChessPiece selectedPiece = gameStatusListener.getSelectedPiece();
            handlePieceMove(clickedPosition);
            if (selectedPiece.getType() == PieceType.PAWN && promotionLogic.isAtPromotionRank(clickedPosition)) {
                promotionLogic.promotePawn(selectedPiece, clickedPosition);
            }
            if(checkDraw()){
                gameEventListener.onGameDraw();
            }
        } else {
            notifyInvalidMoveAttempted("No piece selected and no target piece at clicked position");
        }
    }

    private void handlePieceSelection(ChessPiece piece, Position clickedPosition) {
        if (isSelectable(piece)) {
            gameStatusListener.setSelectedPiece(piece);
            gameEventListener.highlightPossibleMoves(piece);
            castlingLogic.castlingJudgeLogic(piece, clickedPosition);
        } else {
            notifyInvalidMoveAttempted("Invalid move: Piece not selectable.");
        }
    }


    private void handlePieceMove(Position clickedPosition) {
        ChessPiece selectedPiece = gameStatusListener.getSelectedPiece();
        if (selectedPiece == null) {
            notifyInvalidMoveAttempted("Invalid move: No piece selected.");
            return;
        }
        if (selectedPiece.getType() == PieceType.KING && isPositionUnderThreat(clickedPosition, selectedPiece.getColor())) {
            notifyInvalidMoveAttempted("Invalid move: King cannot move to a threatened position.");
            return;
        }
        if (gameStatusListener.isAvailableMoveTarget(clickedPosition, this)) {
            executeMove(selectedPiece, clickedPosition);
            selectedPiece.setMoved(true);
            if (selectedPiece.getType() == PieceType.PAWN && promotionLogic.isAtPromotionRank(clickedPosition)) {
                promotionLogic.promotePawn(selectedPiece, clickedPosition);
            }
            chessGameTurn.nextTurn();
            gameStatusListener.setSelectedPiece(null);
            chessObserver.setGameState("Piece moved to " + clickedPosition);
        } else {
            notifyInvalidMoveAttempted("Invalid move: Target position not available.");
        }
    }

    private boolean isPositionUnderThreat(Position clickedPosition, Color color) {
        List<ChessPiece> opponentPieces = gameStatusListener.getChessPieces().stream()
                .filter(piece -> piece.getColor() != color)
                .toList();

        for (ChessPiece piece : opponentPieces) {
            List<Position> validMoves = calculateMovesForPiece(piece);
            if (validMoves.contains(clickedPosition)) {
                return true; // 주어진 위치가 상대방의 말에 의해 위협받고 있음
            }
        }
        return false; // 주어진 위치가 안전함
    }

    public boolean isKingInCheck(Color color) {
        List<ChessPiece> chessPieces = gameStatusListener.getChessPieces();
        Optional<ChessPiece> king = chessPieces.stream()
                .filter(piece -> piece != null && piece.getType() == PieceType.KING && piece.getColor() == color)
                .findFirst();
        if (king.isEmpty()) {
            throw new IllegalStateException("King not found for color " + color);
        }
        ChessPiece king1 = king.get();
        List<ChessPiece> chessPieces1 = gameStatusListener.getChessPieces();
        return chessPieces1.stream()
                .filter(piece -> piece != null && piece.getColor() != king1.getColor())
                .anyMatch(piece -> calculateMovesForPiece(piece).contains(king1.getPosition()));
    }






    private void notifyInvalidMoveAttempted(String reason) {
        if (gameEventListener != null) {
            gameEventListener.onInvalidMoveAttempted(reason);
        }
    }

    public boolean checkDraw(){
        Color currentPlayerColor = chessGameTurn.getCurrentPlayerColor();
        return drawCondition.isDraw(currentPlayerColor);
    }
    private void executeMove(ChessPiece selectedPiece, Position clickedPosition) {
        Command moveCommand = new MoveCommand(selectedPiece, selectedPiece.getPosition(), clickedPosition, gameStatusListener, gameUtils);
        Optional<ChessPiece> opponentPiece = gameUtils.findPieceAtPosition(gameStatusListener, clickedPosition);
        opponentPiece.ifPresent(p -> {
            ChessPiece chessPiece = opponentPiece.get();
            onPieceRemovePanel(chessPiece);
        });
        gameEventListener.onPieceMoved(clickedPosition, selectedPiece);
        commandInvoker.executeCommand(moveCommand);
        if(afterCastling){
            handleCastlingMove(selectedPiece);
            setAfterCastling(false);
            updateGameStateAfterMove(selectedPiece, clickedPosition);
        }
        gameEventListener.clearHighlights();

        gameStatusListener.setSelectedPiece(null);

        handleEnPassant(selectedPiece, clickedPosition);
        updateGameStateAfterMove(selectedPiece, clickedPosition);
    }

    private void handleCastlingMove(ChessPiece king) {
        if (!castlingLogic.isQueenSide()) {
            moveRookForCastling(new Position(7, king.getPosition().y()), new Position(5, king.getPosition().y()));
        } else {
            moveRookForCastling(new Position(0, king.getPosition().y()), new Position(3, king.getPosition().y()));
        }
    }

    private void moveRookForCastling(Position from, Position to) {
        Optional<ChessPiece> rook = gameStatusListener.getChessPieceAt(from);
        if (rook.isPresent()) {
            Command moveRookCommand = new MoveCommand(rook.get(), from, to, gameStatusListener, gameUtils);
            gameEventListener.onPieceMoved(to, rook.get());
            commandInvoker.executeCommand(moveRookCommand);
        }
    }


    public void updateGameStateAfterMove(ChessPiece selectedPiece, Position clickedPosition) {
        boolean isPawnMove = selectedPiece.getType() == PieceType.PAWN;
        boolean isCapture = gameStatusListener.getChessPieceAt(clickedPosition).isPresent();
        gameStatusListener.updateMoveWithoutPawnOrCaptureCount(isPawnMove, isCapture);
    }



    @Override
    public boolean isKingInCheckAfterMove(ChessPiece piece, Position clickedPosition) {
        return isKingInCheck(piece.getColor());
    }

    private void handleEnPassant(ChessPiece selectedPiece, Position clickedPosition) {
        if (checkEnPassantCondition(selectedPiece, clickedPosition)) {
            Position targetPawnPosition = new Position(clickedPosition.x(), selectedPiece.getColor() == Color.WHITE ? clickedPosition.y() + 1 : clickedPosition.y() - 1);
            Optional<ChessPiece> targetPawn = gameStatusListener.getChessPieceAt(targetPawnPosition);
            if (targetPawn.isPresent() && targetPawn.get().getType() == PieceType.PAWN) {
                gameStatusListener.removeChessPiece(targetPawn.get());
                onPieceRemovePanel(targetPawn.get());
            }
            gameEventListener.onPieceMoved(clickedPosition, selectedPiece);
            gameEventListener.clearHighlights();
        }
    }

    private boolean checkEnPassantCondition(ChessPiece selectedPiece, Position moveTo) {
        if (selectedPiece.getType() != PieceType.PAWN) return false;
        int direction = selectedPiece.getColor() == Color.WHITE ? 1 : -1;
        Position adjacentPawnPosition = new Position(moveTo.x(), moveTo.y() + direction);
        Optional<ChessPiece> adjacentPawn = gameStatusListener.getChessPieceAt(adjacentPawnPosition);
        if (adjacentPawn.isEmpty()) return false;
        ChessPiece adjacent = adjacentPawn.get();
        return adjacent.getType() == PieceType.PAWN;
    }

    public void onPieceRemovePanel(ChessPiece piece) {
        JPanel panel = gameEventListener.getPanelAtPosition(piece.getPosition());
        panel.removeAll();
        panel.revalidate();
        panel.repaint();
    }


    boolean isFriendlyPieceAtPosition(Position position, ChessPiece selectedPiece) {
        Optional<ChessPiece> pieceAtPosition = gameUtils.findPieceAtPosition(gameStatusListener, position);
        return pieceAtPosition.isPresent() && pieceAtPosition.get().getColor() == selectedPiece.getColor();
    }

    private boolean isSelectable(ChessPiece piece) {
        return chessGameTurn.getCurrentPlayerColor() == piece.getColor();
    }


    public List<Position> calculateMovesForPiece(ChessPiece piece) {
        return piece.calculateMoves(gameStatusListener, gameUtils);
    }


}
