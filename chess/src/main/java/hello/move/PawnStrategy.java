package hello.move;

import hello.*;
import hello.core.Player;
import hello.gameobject.ChessBoard;
import hello.gameobject.ChessPiece;

import java.util.ArrayList;
import java.util.List;

public class PawnStrategy implements MoveStrategy {

    private static final int WHITE_START_ROW = 6;
    private static final int BLACK_START_ROW = 1;
    private static final int MOVE_ONE_STEP = 1;
    private static final int MOVE_TWO_STEPS = 2;

    @Override
    public List<Position> calculateMoves(ChessBoard chessBoard, ChessPiece piece) {
        List<Position> validMoves = new ArrayList<>();
        Position position = piece.getPosition();
        Player.Color color = piece.getColor();

        calculateStandardMoves(chessBoard, piece, validMoves, position, color);

        calculateAttackMoves(chessBoard, piece, validMoves, position, color);

        return validMoves;
    }

    private void calculateStandardMoves(ChessBoard chessBoard, ChessPiece piece, final List<Position> validMoves, Position position, Player.Color color) {
        int direction = (color == Player.Color.WHITE) ? -MOVE_ONE_STEP : MOVE_ONE_STEP;
        int startY = position.getY();

        // Check single step
        addValidMoveIfPossible(chessBoard, validMoves, position, direction, MOVE_ONE_STEP);

        // Check double step for first move
        if (isOnStartRow(color, startY)) {
            addValidMoveIfPossible(chessBoard, validMoves, position, direction, MOVE_TWO_STEPS);
        }
    }

    private void calculateAttackMoves(ChessBoard chessBoard, ChessPiece piece, final List<Position> validMoves, Position position, Player.Color color) {
        int direction = (color == Player.Color.WHITE) ? -MOVE_ONE_STEP : MOVE_ONE_STEP;
        int startX = position.getX();
        int startY = position.getY();

        int[] attackOffsets = {-1, 1}; // Attack left and right
        for (int xOffset : attackOffsets) {
            Position attackPos = new Position(startX + xOffset, startY + direction);
            if (chessBoard.getDistanceManager().isPositionOccupiedByOpponent(attackPos, color, chessBoard)){
                validMoves.add(attackPos);
            }
        }
    }

    private void addValidMoveIfPossible(ChessBoard chessBoard, final List<Position> validMoves, Position position, int directionY, int steps) {
        Position potentialPosition = new Position(position.getX(), position.getY() + (directionY * steps));
        if (chessBoard.getDistanceManager().isPositionEmpty(potentialPosition, chessBoard)) {
            validMoves.add(potentialPosition);
        }
    }

    private boolean isOnStartRow(Player.Color color, int row) {
        return (color == Player.Color.WHITE && row == WHITE_START_ROW) ||
                (color == Player.Color.BLACK && row == BLACK_START_ROW);
    }

}
