package game.status;

import game.GameUtils;
import game.Position;
import game.core.Color;
import game.core.GameTurnListener;
import game.factory.Type;
import game.object.ChessGameState;
import game.factory.ChessPiece;
import game.object.GameLogicActions;
import game.object.GameStatusListener;
import game.strategy.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VictoryCondition {

    private GameStatusListener chessGameState;
    private GameUtils gameUtils;
    private GameTurnListener chessGameTurn;


    public void setVictoryCondition(GameStatusListener chessGameState, GameUtils gameUtils, GameTurnListener chessGameTurn) {
        this.chessGameState = chessGameState;
        this.gameUtils = gameUtils;
        this.chessGameTurn = chessGameTurn;
    }
    public VictoryCondition() {

    }

    public boolean isCheckMate() {
        ChessPiece king = chessGameState.getKing(chessGameTurn.getCurrentPlayerColor());
        if (king == null) {
            throw new IllegalStateException("King not found for current player");
        }
        if (!isKingInCheck(king)) {
            return false;
        }
        Set<Position> validMoves = new HashSet<>(new KingStrategy().calculateMoves(chessGameState, king, gameUtils));
        for (Position move : validMoves) {
            if (!isPositionUnderThreat(move, king.getColor())) {
                return false; // 왕이 체크 상태에서 벗어날 수 있는 위치가 있음
            }
        }
        return true;
    }

    private boolean isKingInCheck(ChessPiece king) {
        List<ChessPiece> opponentPieces = chessGameState.getChessPieces().stream()
                .filter(piece -> piece.getColor() != king.getColor())
                .toList();

        for (ChessPiece opponentPiece : opponentPieces) {
            Set<Position> opponentMoves = calculateMovesForPiece(opponentPiece);
            if (opponentMoves.contains(king.getPosition())) {
                return true; // 왕의 위치가 상대방의 말에 의해 공격받을 수 있으므로, 왕은 체크 상태입니다.
            }
        }
        return false; // 왕의 위치가 안전하므로, 왕은 체크 상태가 아닙니다.
    }

    public boolean isPositionUnderThreat(Position position, Color color) {
        List<ChessPiece> opponentPieces = chessGameState.getChessPieces().stream()
                .filter(piece -> piece.getColor() != color)
                .toList();

        for (ChessPiece piece : opponentPieces) {
            Set<Position> validMoves = calculateMovesForPiece(piece);
            if (validMoves.contains(position)) {
                return true; // 주어진 위치가 상대방의 말에 의해 위협받고 있음
            }
        }
        return false; // 주어진 위치가 안전함
    }

    private Set<Position> calculateMovesForPiece(ChessPiece piece) {
        MoveStrategy strategy = switch (piece.getType()) {
            case PAWN -> new PawnStrategy();
            case KNIGHT -> new KnightStrategy();
            case BISHOP -> new BishopStrategy();
            case ROOK -> new RookStrategy();
            case QUEEN -> new QueenStrategy();
            case KING -> new KingStrategy();
        };
        return new HashSet<>(strategy.calculateMoves(chessGameState, piece, gameUtils));
    }
}

