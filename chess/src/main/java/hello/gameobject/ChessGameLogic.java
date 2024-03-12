package hello.gameobject;

import hello.GameEventListener;
import hello.GameLog;
import hello.GameUtils;
import hello.Position;
import hello.command.Command;
import hello.command.CommandInvoker;
import hello.command.MoveCommand;
import hello.core.ChessGameTurn;
import hello.core.Player;
import hello.strategy.*;

import javax.swing.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ChessGameLogic {
    private GameEventListener gameEventListener;

    public void setGameEventListener(GameEventListener gameEventListener) {
        this.gameEventListener = gameEventListener;
    }

    public ChessGameLogic(ChessGameTurn chessGameTurn, CommandInvoker commandInvoker, ChessGameState chessGameState, GameLog gameLog) {
        this.chessGameTurn = chessGameTurn;
        this.commandInvoker = commandInvoker;
        this.chessGameState = chessGameState;

    }

    private final GameUtils gameUtils = new GameUtils();
    private final ChessGameTurn chessGameTurn;
    private final CommandInvoker commandInvoker;
    private final ChessGameState chessGameState;

    public void handleSquareClick(int x, int y) {
        // 클릭된 위치 확인
        Position clickedPosition = new Position(x, y);
        // 해당 위치에 있는 체스 말을 찾음
        Optional<ChessPiece> targetPiece = gameUtils.findPieceAtPosition(x, y, chessGameState);
        System.out.println(chessGameState.getSelectedPiece());
        if (targetPiece.isPresent() && isSelectable(targetPiece.get())) {
            chessGameState.setSelectedPiece(targetPiece.get());
            ChessPiece selectedPiece = chessGameState.getSelectedPiece();
            System.out.println("저장한 말 좌표 :" + selectedPiece.getPosition().getX() + " " + selectedPiece.getPosition().getY());
            chessBoardUI.highlightPossibleMoves(selectedPiece, this, chessBoardUI);
            selectPiece(selectedPiece);
        } else if (chessGameState.getSelectedPiece() != null && isAvailableMoveTarget(clickedPosition)) {
            // 이동 가능한 위치일 경우에만 이동을 실행합니다.
            ChessPiece selectedPiece = chessGameState.getSelectedPiece();
            executeMove(selectedPiece, clickedPosition);
            chessGameTurn.nextPlayer();
            chessGameState.setSelectedPiece(null);
        } else {
            notifyInvalidMoveAttempted();
        }
    }


    private void notifyInvalidMoveAttempted() {
        Optional.ofNullable(gameEventListener).ifPresent(GameEventListener::onInvalidMoveAttempted);
    }

    private void executeMove(ChessPiece selectedPiece, Position clickedPosition) {
        Command moveCommand = new MoveCommand(selectedPiece, selectedPiece.getPosition(), clickedPosition,
                chessGameState, gameUtils);

        commandInvoker.setCommand(moveCommand);
        Optional<ChessPiece> opponentPiece = gameUtils.
                findPieceAtPosition(clickedPosition.getX(), clickedPosition.getY(), chessGameState);
        opponentPiece.ifPresent(p -> {
            ChessPiece chessPiece = opponentPiece.get();
            onPieceRemovePanel(chessPiece);
        });

        chessBoardUI.onPieceMoved(selectedPiece.getPosition(), clickedPosition, selectedPiece);

        commandInvoker.executeCommand();

        chessBoardUI.clearHighlights(chessBoardUI);

        if (checkEnPassantCondition(selectedPiece, clickedPosition)) {
            // 앙파썽 대상 기물 제거
            Position targetPawnPosition = new Position(clickedPosition.getX(), selectedPiece.getColor() == Player.Color.WHITE ? clickedPosition.getY() + 1 : clickedPosition.getY() - 1);
            ChessPiece targetPawn = chessGameState.getChessPieceAt(targetPawnPosition);
            if (targetPawn != null && targetPawn.getType() == ChessPiece.Type.PAWN) {
                chessGameState.removeChessPiece(targetPawn);
                onPieceRemovePanel(targetPawn);
            }

            // UI 업데이트
            chessBoardUI.onPieceMoved(selectedPiece.getPosition(), clickedPosition, selectedPiece);
            chessBoardUI.clearHighlights(chessBoardUI);
        }

    }

    private boolean checkEnPassantCondition(ChessPiece selectedPiece, Position moveTo) {
        if (selectedPiece.getType() != ChessPiece.Type.PAWN) {
            return false;
        }
        int direction = selectedPiece.getColor() == Player.Color.WHITE ? 1 : -1;
        Position adjacentPawnPosition = new Position(moveTo.getX(), moveTo.getY() + direction);
        ChessPiece adjacentPawn = chessGameState.getChessPieceAt(adjacentPawnPosition);
        return adjacentPawn != null && adjacentPawn.getType() == ChessPiece.Type.PAWN;
    }

    public void onPieceRemovePanel(ChessPiece piece) {
        JPanel panel = chessBoardUI.getPanelAtPosition(piece.getPosition());
        panel.removeAll();
        panel.revalidate();
        panel.repaint();
    }


    private boolean isAvailableMoveTarget(Position position) {
        ChessPiece selectedPiece = chessGameState.getSelectedPiece();
        List<Position> validMoves = calculateMovesForPiece(selectedPiece);

        return validMoves.contains(position) && !isFriendlyPieceAtPosition(position, selectedPiece);
    }

    private boolean isFriendlyPieceAtPosition(Position position, ChessPiece selectedPiece) {
        Optional<ChessPiece> pieceAtPosition = gameUtils.findPieceAtPosition(position.getX(), position.getY(), chessGameState);
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
