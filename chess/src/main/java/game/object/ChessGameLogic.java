package game.object;

import game.GameLog;
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

//TODO: 캐슬링 조건 검사및 수행로직 구현
public class ChessGameLogic  implements GameLogicActions {
    private GameEventListener gameEventListener;
    private GameStatusListener gameStatusListener;

    public void setGameEventListener(GameEventListener gameEventListener,GameStatusListener gameStatusListener) {
        this.gameEventListener = gameEventListener;
        this.gameStatusListener = gameStatusListener;
    }

    private final GameLog gameLog;

    public ChessGameLogic(ChessGameTurn chessGameTurn, CommandInvoker commandInvoker, GameLog gameLog) {
        this.chessGameTurn = chessGameTurn;
        this.commandInvoker = commandInvoker;
        this.gameLog = gameLog;
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
            handlePieceMove(clickedPosition);
        } else {
            notifyInvalidMoveAttempted("No piece selected and no target piece at clicked position");
        }
    }

    private void handlePieceSelection(ChessPiece piece, Position clickedPosition) {
        if (isSelectable(piece)) {
            gameStatusListener.setSelectedPiece(piece);
            gameEventListener.highlightPossibleMoves(piece);
//            castlingJudgeLogic(piece, clickedPosition);
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
        if (gameStatusListener.isAvailableMoveTarget(clickedPosition, this)) {
            executeMove(selectedPiece, clickedPosition);
            chessGameTurn.nextPlayer();
            gameStatusListener.setSelectedPiece(null);
        } else {
            notifyInvalidMoveAttempted("Invalid move: Target position not available.");
        }
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

        handleEnPassant(selectedPiece, clickedPosition);
        updateGameStateAfterMove(selectedPiece, clickedPosition);
    }

    private void updateGameStateAfterMove(ChessPiece selectedPiece, Position clickedPosition) {
        boolean isPawnMove = selectedPiece.getType() == Type.PAWN;
        boolean isCapture = gameStatusListener.getChessPieceAt(clickedPosition).isPresent();
        gameStatusListener.updateMoveWithoutPawnOrCaptureCount(isPawnMove, isCapture);
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
        return chessGameTurn.getCurrentPlayerColor() == piece.getColor();
    }


    public List<Position> calculateMovesForPiece(ChessPiece piece) {
        return piece.calculateMoves(gameStatusListener, gameUtils);
    }


}
