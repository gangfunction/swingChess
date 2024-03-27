package game.object;

import game.GameUtils;
import game.Position;
import game.command.Command;
import game.command.CommandInvoker;
import game.command.MoveCommand;
import game.core.ChessGameTurn;
import game.core.Color;
import game.factory.ChessPiece;
import game.factory.Type;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChessGameLogic  implements GameLogicActions {
    private GameEventListener gameEventListener;
    private GameStatusListener gameStatusListener;

    private final CastlingLogic castlingLogic;
    private final PromotionLogic promotionLogic;
    boolean afterCastling = false;
    boolean queenCastleSide = false;

    public void setGameEventListener(GameEventListener gameEventListener,GameStatusListener gameStatusListener) {
        this.gameEventListener = gameEventListener;
        this.gameStatusListener = gameStatusListener;
    }


    public ChessGameLogic(ChessGameTurn chessGameTurn, CommandInvoker commandInvoker, CastlingLogic castlingLogic,PromotionLogic promotionLogic) {
        this.chessGameTurn = chessGameTurn;
        this.commandInvoker = commandInvoker;
        this.castlingLogic = castlingLogic;
        this.promotionLogic = promotionLogic;
    }

    private final GameUtils gameUtils = new GameUtils();
    private final ChessGameTurn chessGameTurn;
    private final CommandInvoker commandInvoker;

    public void handleSquareClick(int x, int y) {
        Position clickedPosition = new Position(x, y);
        Optional<ChessPiece> targetPiece = gameUtils.findPieceAtPosition(gameStatusListener, clickedPosition);
        if (targetPiece.isPresent() && gameStatusListener.getSelectedPiece() == null) {
            ChessPiece chessPiece = targetPiece.get();
            handlePieceSelection(chessPiece, clickedPosition);
        } else if (gameStatusListener.getSelectedPiece() != null) {
            ChessPiece selectedPiece = gameStatusListener.getSelectedPiece();
            handlePieceMove(clickedPosition);
            promotionLogic.promotePawn(selectedPiece, clickedPosition);

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
            notifyInvalidMoveAttempted("Invalid move:  Piece not selectable.");
        }
    }

    public List<Position> getPositions(ChessPiece piece, Position clickedPosition) {
        Position currentPosition1 = piece.getPosition();
        List<Position> path3 = new ArrayList<>();

        // 킹 사이드 캐슬링 경로
        if (clickedPosition.x() > currentPosition1.x()) {
            for (int x1 = currentPosition1.x() + 1; x1 <= clickedPosition.x(); x1++) {
                path3.add(new Position(x1, currentPosition1.y()));
            }
        } else {
            for (int x1 = currentPosition1.x() - 1; x1 >= clickedPosition.x(); x1--) {
                path3.add(new Position(x1, currentPosition1.y()));
            }
        }

        return path3;
    }

    private void handlePieceMove(Position clickedPosition) {
        ChessPiece selectedPiece = gameStatusListener.getSelectedPiece();
        System.out.println(gameStatusListener.isAvailableMoveTarget(clickedPosition, this));
        if(selectedPiece == null){
            notifyInvalidMoveAttempted("Invalid move: No piece selected.");
            return;
        }
        if(selectedPiece.getType() == Type.KING && isPositionUnderThreat(clickedPosition, selectedPiece.getColor())){
            notifyInvalidMoveAttempted("Invalid move: King cannot move to a threatened position.");
            return;
        }
        if (gameStatusListener.isAvailableMoveTarget(clickedPosition, this)) {
            executeMove(selectedPiece, clickedPosition);
            selectedPiece.setMoved(true);
            chessGameTurn.nextTurn();
            gameStatusListener.setSelectedPiece(null);
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
                .filter(piece -> piece != null && piece.getType() == Type.KING && piece.getColor() == color)
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

    private void executeMove(ChessPiece selectedPiece, Position clickedPosition) {
        Command moveCommand = new MoveCommand(selectedPiece, selectedPiece.getPosition(), clickedPosition, gameStatusListener, gameUtils);
        Optional<ChessPiece> opponentPiece = gameUtils.findPieceAtPosition(gameStatusListener, clickedPosition);
        opponentPiece.ifPresent(p -> {
            ChessPiece chessPiece = opponentPiece.get();
            onPieceRemovePanel(chessPiece);
        });
        gameEventListener.onPieceMoved(clickedPosition, selectedPiece);
        commandInvoker.executeCommand(moveCommand);
        gameEventListener.clearHighlights();
        if(afterCastling){
            if(!queenCastleSide){
                gameEventListener.onPieceMoved(new Position(3, selectedPiece.getPosition().y()), gameStatusListener.getChessPieceAt(new Position(0, selectedPiece.getPosition().y())).get());
                new MoveCommand(gameStatusListener.getChessPieceAt(new Position(0, selectedPiece.getPosition().y())).get(), new Position(0, selectedPiece.getPosition().y()), new Position(3, selectedPiece.getPosition().y()), gameStatusListener, gameUtils).execute();
                setAfterCastling(false);
            }
            else{
                gameEventListener.onPieceMoved(new Position(5, selectedPiece.getPosition().y()), gameStatusListener.getChessPieceAt(new Position(7, selectedPiece.getPosition().y())).get());
                new MoveCommand(gameStatusListener.getChessPieceAt(new Position(7, selectedPiece.getPosition().y())).get(), new Position(7, selectedPiece.getPosition().y()), new Position(5, selectedPiece.getPosition().y()), gameStatusListener, gameUtils).execute();
                setAfterCastling(false);
            }

        }
        gameStatusListener.setSelectedPiece(null);

        handleEnPassant(selectedPiece, clickedPosition);
        updateGameStateAfterMove(selectedPiece, clickedPosition);
    }


    public void updateGameStateAfterMove(ChessPiece selectedPiece, Position clickedPosition) {
        boolean isPawnMove = selectedPiece.getType() == Type.PAWN;
        boolean isCapture = gameStatusListener.getChessPieceAt(clickedPosition).isPresent();
        gameStatusListener.updateMoveWithoutPawnOrCaptureCount(isPawnMove, isCapture);
    }

    @Override
    public void setAfterCastling(boolean afterCastling) {
        this.afterCastling = afterCastling;
    }

    @Override
    public void setQueenCastleSide(boolean b) {
        this.queenCastleSide = b;

    }

    private void handleEnPassant(ChessPiece selectedPiece, Position clickedPosition) {
        if (checkEnPassantCondition(selectedPiece, clickedPosition)) {
            Position targetPawnPosition = new Position(clickedPosition.x(), selectedPiece.getColor() == Color.WHITE ? clickedPosition.y() + 1 : clickedPosition.y() - 1);
            Optional<ChessPiece> targetPawn = gameStatusListener.getChessPieceAt(targetPawnPosition);
            if (targetPawn.isPresent() && targetPawn.get().getType() == Type.PAWN) {
                gameStatusListener.removeChessPiece(targetPawn.get());
                onPieceRemovePanel(targetPawn.get());
            }
            gameEventListener.onPieceMoved(clickedPosition, selectedPiece);
            gameEventListener.clearHighlights();
        }
    }

    private boolean checkEnPassantCondition(ChessPiece selectedPiece, Position moveTo) {
        if (selectedPiece.getType() != Type.PAWN) return false;
        int direction = selectedPiece.getColor() == Color.WHITE ? 1 : -1;
        Position adjacentPawnPosition = new Position(moveTo.x(), moveTo.y() + direction);
        Optional<ChessPiece> adjacentPawn = gameStatusListener.getChessPieceAt(adjacentPawnPosition);
        if (adjacentPawn.isEmpty()) return false;
        ChessPiece adjacent = adjacentPawn.get();
        return adjacent.getType() == Type.PAWN;
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
        boolean answer = chessGameTurn.getCurrentPlayerColor() == piece.getColor();
        System.out.println(chessGameTurn.getCurrentPlayerColor() + "랑 비교" + piece.getColor());
        System.out.println(answer+"is not selectable");
        return answer;
    }


    public List<Position> calculateMovesForPiece(ChessPiece piece) {
        return piece.calculateMoves(gameStatusListener, gameUtils);
    }


}
