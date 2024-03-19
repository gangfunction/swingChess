package game.object;

import game.GameEventListener;
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
import game.strategy.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

//TODO: 캐슬링 조건 검사및 수행로직 구현
public class ChessGameLogic implements GameLogicActions {
    private GameEventListener gameEventListener;

    public void setGameEventListener(GameEventListener gameEventListener) {
        this.gameEventListener = gameEventListener;
    }

    private final GameLog gameLog;
    public ChessGameLogic(ChessGameTurn chessGameTurn, CommandInvoker commandInvoker, ChessGameState chessGameState,GameLog gameLog) {
        this.chessGameTurn = chessGameTurn;
        this.commandInvoker = commandInvoker;
        this.chessGameState = chessGameState;
        this.gameLog = gameLog;
    }

    private final GameUtils gameUtils = new GameUtils();
    private final ChessGameTurn chessGameTurn;
    private final CommandInvoker commandInvoker;
    private final ChessGameState chessGameState;

    public void handleSquareClick(int x, int y) {
        Position clickedPosition = new Position(x, y);
        Optional<ChessPiece> targetPiece = gameUtils.findPieceAtPosition(x, y, chessGameState);
        System.out.println(chessGameState.getSelectedPiece());
        if (targetPiece.isPresent()) {
            handlePieceSelection(targetPiece.get(), clickedPosition);
        } else if (chessGameState.getSelectedPiece() != null && chessGameState.isAvailableMoveTarget(clickedPosition, this)) {
            handlePieceMove(clickedPosition);
        } else {
            notifyInvalidMoveAttempted("No piece selected and no target piece at clicked position");
        }
    }
    private void handlePieceSelection(ChessPiece piece, Position clickedPosition) {
        if (isSelectable(piece)) {
            chessGameState.setSelectedPiece(piece);
            chessBoardUI.highlightPossibleMoves(piece);
            if (isCastlingAttempt(piece, clickedPosition)) {
                Position kingTargetPosition, rookTargetPosition;
                if (clickedPosition.x() > piece.getPosition().x()) {
                    kingTargetPosition = new Position(6, piece.getPosition().y());
                    rookTargetPosition = new Position(5, piece.getPosition().y());
                } else {
                    kingTargetPosition = new Position(2, piece.getPosition().y());
                    rookTargetPosition = new Position(3, piece.getPosition().y());
                }
                chessBoardUI.highlightMoves(List.of( kingTargetPosition, rookTargetPosition));
            }
        } else {
            notifyInvalidMoveAttempted("Invalid move: Piece not selectable.");
        }
    }
    private void handlePieceMove(Position clickedPosition) {
        ChessPiece selectedPiece = chessGameState.getSelectedPiece();
        if (chessGameState.isAvailableMoveTarget(clickedPosition, this)) {
            executeMove(selectedPiece, clickedPosition);
            chessGameTurn.nextPlayer();
            chessGameState.setSelectedPiece(null);
        } else {
            notifyInvalidMoveAttempted("Invalid move: Target position not available.");
        }
    }

    private boolean isCastlingAttempt(ChessPiece targetPiece, Position moveTo) {
        if (targetPiece.getType() != Type.KING) {
            return false;
        }
        System.out.println("캐슬링 시도");
        System.out.println("moveTo:" + moveTo.x() + " " + moveTo.y());
        System.out.println("2" + new Position(2, targetPiece.getPosition().y()).y() + " 6" + new Position(6, targetPiece.getPosition().y()).y());
        return targetPiece.isMoved() && chessGameState.isRookUnmovedForCastling(targetPiece.getColor(), moveTo) && !isKingInCheck(targetPiece.getColor()) && isPathClearForCastling(targetPiece, moveTo) && isPathUnderAttack(targetPiece, moveTo);
    }

    private boolean isPathUnderAttack(ChessPiece targetPiece, Position moveTo) {
        List<ChessPiece> opponentPieces = chessGameState.getChessPieces().stream()
                .filter(piece -> piece.getColor() != targetPiece.getColor())
                .toList();
        List<Position> path = calculateCastlingPath(targetPiece.getPosition(), moveTo);

        for (Position position : path) {
            for (ChessPiece opponentPiece : opponentPieces) {
                List<Position> attackPositions = calculateMovesForPiece(opponentPiece);
                if (attackPositions.contains(position)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isPathClearForCastling(ChessPiece targetPiece, Position moveTo) {
        List<Position> path = calculateCastlingPath(targetPiece.getPosition(), moveTo);
        for (Position position : path) {
            if (chessGameState.getChessPieceAt(position) != null) {
                return false; // 경로상에 말이 있어 캐슬링을 수행할 수 없음
            }
        }
        return true; // 경로가 명확하여 캐슬링을 수행할 수 있음
    }

    public List<Position> calculateCastlingPath(Position currentPosition, Position moveTo) {
        List<Position> path = new ArrayList<>();

        // 킹 사이드 캐슬링 경로
        if (moveTo.x() > currentPosition.x()) {
            for (int x = currentPosition.x() + 1; x <= moveTo.x(); x++) {
                path.add(new Position(x, currentPosition.y()));
            }
        } else {
            for (int x = currentPosition.x() - 1; x >= moveTo.x(); x--) {
                path.add(new Position(x, currentPosition.y()));
            }
        }

        return path;
    }

    public boolean isKingInCheck(Color color) {
        List<ChessPiece> chessPieces = chessGameState.getChessPieces();
        Optional<ChessPiece> king = chessPieces.stream()
                .filter(piece -> piece != null && piece.getType() == Type.KING && piece.getColor() == color)
                .findFirst();
        if (king.isEmpty()) {
            throw new IllegalStateException("King not found for color " + color);
        }
        ChessPiece king1 = king.get();
        List<ChessPiece> chessPieces1 = chessGameState.getChessPieces();
        return chessPieces1.stream()
                .filter(piece -> piece != null && piece.getColor() != king1.getColor())
                .anyMatch(piece -> calculateMovesForPiece(piece).contains(king1.getPosition()));
    }

    private void performCastling(ChessPiece king, Position moveTo) {
        if (isPathUnderAttack(king, moveTo) && isPathClearForCastling(king, moveTo)) {
            // 킹과 룩의 새로운 위치 계산
            Position rookOriginalPosition, rookNewPosition;
            if (moveTo.x() > king.getPosition().x()) {
                // 킹 사이드 캐슬링
                rookOriginalPosition = new Position(7, king.getPosition().y());
                rookNewPosition = king.getPosition().add(1, 0); // 킹 옆으로 이동
            } else {
                // 퀸 사이드 캐슬링
                rookOriginalPosition = new Position(0, king.getPosition().y());
                rookNewPosition = king.getPosition().add(-1, 0); // 킹 옆으로 이동
            }
            ChessPiece rook = gameUtils.findPieceAtPosition(rookOriginalPosition.x(), rookOriginalPosition.y(), chessGameState).get();

            // 킹과 룩 이동
            chessGameState.movePiece(king, moveTo);
            chessGameState.movePiece(rook, rookNewPosition);
            chessGameTurn.nextPlayer();
        } else {
            notifyInvalidMoveAttempted("Cannot perform castling");
        }
    }


    private void notifyInvalidMoveAttempted(String reason) {
        if (gameEventListener != null) {
            gameEventListener.onInvalidMoveAttempted(reason);
        }
    }

    private void executeMove(ChessPiece selectedPiece, Position clickedPosition) {
        Command moveCommand = new MoveCommand(selectedPiece, selectedPiece.getPosition(), clickedPosition, chessGameState, gameUtils);
        Optional<ChessPiece> opponentPiece = gameUtils.findPieceAtPosition(clickedPosition.x(), clickedPosition.y(), chessGameState);
        opponentPiece.ifPresent(p -> {
            ChessPiece chessPiece = opponentPiece.get();
            onPieceRemovePanel(chessPiece);
        });
        chessBoardUI.onPieceMoved(clickedPosition, selectedPiece);
        commandInvoker.executeCommand(moveCommand);
        chessBoardUI.clearHighlights();

        handleEnPassant(selectedPiece, clickedPosition);
        updateGameStateAfterMove(selectedPiece, clickedPosition);
    }

    private void updateGameStateAfterMove(ChessPiece selectedPiece, Position clickedPosition) {
        boolean isPawnMove = selectedPiece.getType() == Type.PAWN;
        boolean isCapture = chessGameState.getChessPieceAt(clickedPosition).isPresent();
        chessGameState.updateMoveWithoutPawnOrCaptureCount(isPawnMove, isCapture);
    }

    private void handleEnPassant(ChessPiece selectedPiece, Position clickedPosition) {
        if (checkEnPassantCondition(selectedPiece, clickedPosition)) {
            Position targetPawnPosition = new Position(clickedPosition.x(), selectedPiece.getColor() == Color.WHITE ? clickedPosition.y() + 1 : clickedPosition.y() - 1);
            Optional<ChessPiece> targetPawn = chessGameState.getChessPieceAt(targetPawnPosition);
            if(targetPawn.isPresent() && targetPawn.get().getType() == Type.PAWN) {
                chessGameState.removeChessPiece(targetPawn.get());
                onPieceRemovePanel(targetPawn.get());
            }
            chessBoardUI.onPieceMoved(clickedPosition, selectedPiece);
            chessBoardUI.clearHighlights();
        }
    }

    private boolean checkEnPassantCondition(ChessPiece selectedPiece, Position moveTo) {
        if (selectedPiece.getType() != Type.PAWN) return false;
        int direction = selectedPiece.getColor() == Color.WHITE ? 1 : -1;
        Position adjacentPawnPosition = new Position(moveTo.x(), moveTo.y() + direction);
        Optional<ChessPiece> adjacentPawn = chessGameState.getChessPieceAt(adjacentPawnPosition);
        if(adjacentPawn.isEmpty()) return false;
        ChessPiece adjacent = adjacentPawn.get();

        return adjacent.getType() == Type.PAWN;
    }

    public void onPieceRemovePanel(ChessPiece piece) {
        JPanel panel = chessBoardUI.getPanelAtPosition(piece.getPosition());
        panel.removeAll();
        panel.revalidate();
        panel.repaint();
    }


    boolean isFriendlyPieceAtPosition(Position position, ChessPiece selectedPiece) {
        Optional<ChessPiece> pieceAtPosition = gameUtils.findPieceAtPosition(position.x(), position.y(), chessGameState);
        return pieceAtPosition.isPresent() && pieceAtPosition.get().getColor() == selectedPiece.getColor();
    }

    public void selectPiece(ChessPiece piece) {
        chessGameState.setSelectedPiece(piece);
    }

    private boolean isSelectable(ChessPiece piece) {
        return chessGameTurn.getCurrentPlayerColor() == piece.getColor();
    }


    public List<Position> calculateMovesForPiece(ChessPiece piece) {
        MoveStrategy moveStrategy = getMoveStrategy(piece);
        return Objects.requireNonNull(moveStrategy).calculateMoves(chessGameState, piece, gameUtils);

    }

    private MoveStrategy getMoveStrategy(ChessPiece piece) {
        return switch (piece.getType()) {
            case PAWN -> new PawnStrategy();
            case KNIGHT -> new KnightStrategy();
            case BISHOP -> new BishopStrategy();
            case QUEEN -> new QueenStrategy();
            case ROOK -> new RookStrategy();
            case KING -> new KingStrategy();
        };
    }

    private ChessBoardUI chessBoardUI;

    public void setChessBoardUI(ChessBoardUI chessBoardUI) {
        this.chessBoardUI = chessBoardUI;
    }


}
