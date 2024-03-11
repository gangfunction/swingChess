package hello;

import hello.command.Command;
import hello.command.CommandInvoker;
import hello.command.MoveCommand;
import hello.core.ChessGame;
import hello.gameobject.ChessBoard;
import hello.gameobject.ChessPiece;
import hello.move.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GameLogic {
    private final ChessBoard chessBoard;
    private final ChessGame chessGame;

    private final CommandInvoker commandInvoker;


    public GameLogic(ChessBoard chessBoard, ChessGame chessGame, CommandInvoker commandInvoker) {
        this.chessBoard = chessBoard;
        this.chessGame = chessGame;
        this.commandInvoker = commandInvoker;
    }

    //
    public void handleSquareClick(int x, int y) {
        Position clickedPosition = new Position(x, y);

        Optional<ChessPiece> targetPiece = chessBoard.getDistanceManager().findPieceAtPosition(x, y, chessBoard);
        if (isAvaliMoveTarget(clickedPosition)) {
            ChessPiece selectedPiece = chessBoard.getSelectedPiece();
            Command moveCommand = new MoveCommand(selectedPiece, selectedPiece.getPosition(), clickedPosition, chessBoard);
            commandInvoker.setCommand(moveCommand);
            commandInvoker.executeCommand();
            chessGame.nextPlayer();
            chessGame.printCurrentPlayer();
            chessBoard.setSelectedPiece(null);

        } else if (targetPiece.isPresent() && chessBoard.getPieceSelectionManager().isSelectable(targetPiece.get())) {
            chessBoard.getPieceSelectionManager().selectPiece(targetPiece.get());
        } else {
            chessBoard.getUIManager().showError();
        }

    }

    boolean isAvaliMoveTarget(Position position) {
        return chessBoard.getHighlightedPositions().contains(position) && chessBoard.getSelectedPiece() != null;
    }

    public void highlightPossibleMoves(ChessPiece piece) {
        // 선택된 말에 대해 가능한 이동을 계산하고 하이라이트
        chessBoard.getUIManager().clearHighlights(chessBoard); // 기존 하이라이트를 지웁니다.
        chessBoard.getHighlightedPositions().clear(); // 하이라이트된 위치 목록을 클리어합니다.

        List<Position> moves = calculateMovesForPiece(piece); // 이동 가능한 위치 계산
        chessBoard.getUIManager().highlightMoves(moves); // 하이라이트를 추가합니다.
        chessBoard.getHighlightedPositions().addAll(moves); // 하이라이트된 위치 목록에 추가합니다.
    }

    List<Position> calculateMovesForPiece(ChessPiece piece) {
        ChessPiece.Type type = piece.getType();
        MoveStrategy moveStrategy = switch (type) {
            case PAWN -> new PawnStrategy();
            case KNIGHT -> new KnightStrategy();
            case BISHOP -> new BishopStrategy();
            case QUEEN -> new QueenStrategy();
            case ROOK -> new RookStrategy();
            case KING -> new KingStrategy();
        };
        return Objects.requireNonNull(moveStrategy).calculateMoves(chessBoard, piece);

    }
}