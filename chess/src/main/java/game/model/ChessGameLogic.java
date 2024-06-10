package game.model;

import game.GameUtils;
import game.Position;
import game.command.CommandInvoker;
import game.command.MoveCommand;
import game.core.ChessGameTurn;
import game.ui.GameEventListener;
import game.util.Color;
import game.core.PlayerManager;
import game.core.factory.ChessPiece;
import game.util.PieceType;
import game.model.castling.CastlingHandler;
import game.model.castling.CastlingLogic;
import game.observer.ChessObserver;
import game.status.DrawCondition;
import game.status.VictoryCondition;
import lombok.Setter;

import java.util.Optional;
import java.util.Set;

public class ChessGameLogic implements GameLogicActions {
    private GameEventListener gameEventListener;
    private GameStatusListener gameStatusListener;

    private final CastlingLogic castlingLogic;
    private final PromotionLogic promotionLogic;
    private final DrawCondition drawCondition;
    private final VictoryCondition victoryCondition;
    private final ChessGameTurn chessGameTurn;
    private final CommandInvoker commandInvoker;
    private final ChessObserver chessObserver;
    private CastlingHandler castlingHandler;
    private final ChessRuleHandler chessRuleHandler;

    @Setter
    private boolean afterCastling = false;
    private PlayerManager playerManager;


    public ChessGameLogic(ChessGameTurn chessGameTurn,
                          CommandInvoker commandInvoker,
                          CastlingLogic castlingLogic,
                          PromotionLogic promotionLogic,
                            PlayerManager playerManager
    ) {
        this.chessGameTurn = chessGameTurn;
        this.commandInvoker = commandInvoker;
        this.castlingLogic = castlingLogic;
        this.promotionLogic = promotionLogic;
        this.victoryCondition = new VictoryCondition();
        this.chessObserver = new ChessObserver();
        this.drawCondition = new DrawCondition();
        this.chessObserver.addObserver(new GameUIObserver());
        this.chessRuleHandler = new ChessRuleHandler();
        this.playerManager = playerManager;
    }

    public void setGameEventListener(GameEventListener gameEventListener, GameStatusListener gameStatusListener) {
        this.gameEventListener = gameEventListener;
        this.gameStatusListener = gameStatusListener;
        this.victoryCondition.setVictoryCondition(gameStatusListener, chessGameTurn);
        this.drawCondition.setDrawCondition(gameStatusListener, this, chessGameTurn);
        this.castlingLogic.setCastlingLogic(gameStatusListener, this);
        this.castlingHandler = new CastlingHandler(gameStatusListener, gameEventListener, chessGameTurn, commandInvoker);

    }


    public void handleSquareClick(int x, int y) {
        Position clickedPosition = new Position(x, y);
        ChessPiece targetPiece = GameUtils.findPieceAtPosition(gameStatusListener, clickedPosition).orElse(null);
        if (targetPiece != null && gameStatusListener.getSelectedPiece() == null) {
            handlePieceSelection(targetPiece, clickedPosition, playerManager);
        } else if (gameStatusListener.getSelectedPiece() != null) {
            handlePieceMove(clickedPosition);
        } else {
            notifyInvalidMoveAttempted("No piece selected and no target piece at clicked position");
        }
    }

    private void handlePieceSelection(ChessPiece piece, Position clickedPosition, PlayerManager playerManager) {
        if (isSelectable(piece, playerManager)) {
            gameStatusListener.setSelectedPiece(piece);
            gameEventListener.highlightPossibleMoves(piece);
            castlingLogic.castlingJudgeLogic(piece, clickedPosition);
        } else {
            notifyInvalidMoveAttempted("Invalid move: Piece not selectable.");
        }
    }
    private boolean canMoveBreakCheck(ChessPiece piece, Position targetPosition) {
        Position originalPosition = piece.getPosition();
        piece.setPosition(targetPosition);
        boolean breaksCheck = !isKingInCheck(piece.getColor());
        piece.setPosition(originalPosition);
        return breaksCheck;
    }

    private void handlePieceMove(Position clickedPosition) {
        ChessPiece selectedPiece = gameStatusListener.getSelectedPiece();
        if (!canMoveSelectedPiece(selectedPiece, clickedPosition)) {
            return;
        }

        if (gameStatusListener.isAvailableMoveTarget(clickedPosition, this)) {
            executeMove(selectedPiece, clickedPosition);
            postMoveActions(selectedPiece, clickedPosition);
        } else {
            notifyInvalidMove("Target position not available.");
        }
    }

    private boolean canMoveSelectedPiece(ChessPiece selectedPiece, Position clickedPosition) {
        if (selectedPiece == null) {
            notifyInvalidMove("No piece selected.");
            return false;
        }
        if (isKingInCheck(selectedPiece.getColor()) &&
                !canMoveBreakCheck(selectedPiece, clickedPosition)) {
            notifyInvalidMove("King is in check and move does not break check.");
            return false;
        }
        return true;
    }


    private void notifyInvalidMove(String reason) {
        if (gameEventListener != null) {
            gameEventListener.onInvalidMoveAttempted(reason);
        }
    }

    private void postMoveActions(ChessPiece selectedPiece, Position clickedPosition) {
        selectedPiece.setMoved(true);
        if (selectedPiece.getType() == PieceType.PAWN && promotionLogic.isAtPromotionRank(clickedPosition)) {
            promotionLogic.promotePawn(selectedPiece, clickedPosition);
        }
        chessGameTurn.nextTurn();
        gameStatusListener.setSelectedPiece(null);
        chessObserver.setGameState("Piece moved to " + clickedPosition);
    }
    
    

    private void notifyInvalidMoveAttempted(String reason) {
        if (gameEventListener != null) {
            gameEventListener.onInvalidMoveAttempted(reason);
        }
    }

    void executeMove(ChessPiece selectedPiece, Position clickedPosition) {
        removeCapturedPieceIfExists(clickedPosition);
        updatePiecePosition(selectedPiece, clickedPosition);
        handleCastlingMove(selectedPiece);
        handleEnPassantCapture(selectedPiece, clickedPosition);
        updateGameStateAfterMove(selectedPiece, clickedPosition);
    }
    private void removeCapturedPieceIfExists(Position clickedPosition) {
        ChessPiece piece = gameStatusListener.getChessPieceAt(clickedPosition);
        if (piece != null) {
            gameStatusListener.removeChessPiece(piece);
            onPieceRemoved(piece);
        }
    }

    private void onPieceRemoved(ChessPiece piece) {
        if (piece == null) return;
        gameEventListener.onPieceRemoved(piece);
        gameEventListener.clearHighlights();
    }

    private void handleEnPassantCapture(ChessPiece selectedPiece, Position clickedPosition) {
        if (chessRuleHandler.checkEnPassantCondition(selectedPiece, clickedPosition, gameStatusListener)) {
            Position targetPawnPosition = getEnPassantTargetPosition(clickedPosition, selectedPiece.getColor());
            ChessPiece targetPawn = gameStatusListener.getChessPieceAt(targetPawnPosition);
            if (targetPawn != null && targetPawn.getType() == PieceType.PAWN) {
                gameStatusListener.removeChessPiece(targetPawn);
                onPieceRemoved(targetPawn);
            }
        }
    }

    private Position getEnPassantTargetPosition(Position clickedPosition, Color color) {
        int direction = color == Color.WHITE ? 1 : -1;
        return new Position(clickedPosition.x(), clickedPosition.y() + direction);
    }
    private void handleCastlingMove(ChessPiece selectedPiece) {
        if (afterCastling) {
            castlingHandler.handleCastlingMove(selectedPiece, castlingLogic.isQueenSide());
            setAfterCastling(false);
        }
    }
    private void updatePiecePosition(ChessPiece selectedPiece, Position clickedPosition) {
        gameEventListener.onPieceMoved(clickedPosition, selectedPiece);
        MoveCommand moveCommand = commandInvoker.executeCommand(selectedPiece, selectedPiece.getPosition(), clickedPosition, gameStatusListener, chessGameTurn);
        commandInvoker.returnCommand(moveCommand);
    }

    public void updateGameStateAfterMove(ChessPiece selectedPiece, Position clickedPosition) {
        boolean isPawnMove = selectedPiece.getType() == PieceType.PAWN;
        boolean isCapture = gameStatusListener.getChessPieceAt(clickedPosition) != null;
        gameStatusListener.updateMoveWithoutPawnOrCaptureCount(isPawnMove, isCapture);
    }

    @Override
    public boolean isKingInCheckAfterMove(ChessPiece piece, Position clickedPosition) {
        return isKingInCheck(piece.getColor());
    }

    @Override
    public boolean isFriendlyPieceAtPosition(Position position, ChessPiece selectedPiece) {
        Optional<ChessPiece> pieceAtPosition = GameUtils.findPieceAtPosition(gameStatusListener, position);
        return pieceAtPosition.isPresent() && pieceAtPosition.get().getColor() == selectedPiece.getColor();
    }

    private boolean isSelectable(ChessPiece piece, PlayerManager playerManager) {
        return playerManager.getCurrentPlayerColor() == piece.getColor();
    }


    public Set<Position> calculateMovesForPiece(ChessPiece piece) {
        return piece.calculateMoves(gameStatusListener);
    }
    public boolean isKingInCheck(Color color){
        return chessRuleHandler.isKingInCheck(color, gameStatusListener);
    }


}
