package game.strategy;

import game.GameUtils;
import game.Position;
import game.util.Color;
import game.core.factory.ChessPiece;
import game.model.GameStatusListener;

import java.util.HashSet;
import java.util.Set;

public class PawnStrategy implements MoveStrategy {

    private static final int WHITE_START_ROW = 6;
    private static final int BLACK_START_ROW = 1;
    private static final int MOVE_ONE_STEP = 1;
    private static final int MOVE_TWO_STEPS = 2;

    private int getMoveDirection(Color color) {
        return (color == Color.WHITE) ? -MOVE_ONE_STEP : MOVE_ONE_STEP;
    }
    @Override
    public Set<Position> calculateMoves(GameStatusListener chessBoard, ChessPiece piece) {
        Set<Position> validMoves = new HashSet<>();
        Position position = piece.getPosition();
        Color color = piece.getColor();

        calculateStandardMoves(chessBoard, validMoves, position, color);
        calculateAttackMoves(chessBoard, validMoves, position, color);
        calculateEnPassantMoves(chessBoard, validMoves, position, color);
        return validMoves;
    }

    private void calculateStandardMoves(GameStatusListener chessBoard, final Set<Position> validMoves, Position position, Color color) {
        int direction = getMoveDirection(color);
        int startY = position.y();

        tryToAddMove(chessBoard, validMoves, position, direction, MOVE_ONE_STEP);

        if (isOnStartRow(color, startY)) {
            tryToAddMove(chessBoard, validMoves, position, direction, MOVE_TWO_STEPS);
        }
    }


    private void calculateEnPassantMoves(GameStatusListener chessBoard, final Set<Position> validMoves, Position position, Color color) {
        int direction = (color == Color.WHITE) ? -MOVE_ONE_STEP : +MOVE_ONE_STEP;
        int enPassantY = position.y() + direction;
        int[] enPassantXs = {position.x() - 1, position.x() + 1};

        for (int enPassantX : enPassantXs) {
            if (enPassantX < 0 || enPassantX >= 8) continue; // 보드를 벗어나는 위치는 제외

            Position targetPawnPosition = new Position(enPassantX, position.y());
            if (GameUtils.isPositionOccupiedByOpponent(targetPawnPosition, color, chessBoard) &&
                    wasPawnMovedTwoSteps(position, chessBoard)) {
                Position enPassantPos = new Position(enPassantX, enPassantY); // 앙파썽 수행 후 폰이 도착할 위치
                validMoves.add(enPassantPos);
            }
        }
    }
    private boolean wasPawnMovedTwoSteps(Position targetPawnPosition, GameStatusListener chessBoard) {
        ChessPiece lastMovedPawn = chessBoard.getLastMovedPiece();
        boolean lastMoveWasDoubleStep = chessBoard.getLastMoveWasDoubleStep();
        if (lastMovedPawn == null || !lastMoveWasDoubleStep) {
            return false;
        }
        Position lastMovedPosition = lastMovedPawn.getPosition();
        int currentX = targetPawnPosition.x();
        int currentY = targetPawnPosition.y();
        return (lastMovedPosition.y() == currentY) && (Math.abs(lastMovedPosition.x() - currentX) == 1);
    }

    private void calculateAttackMoves(GameStatusListener chessBoard, final Set<Position> validMoves, Position position, Color color) {
        int direction = getMoveDirection(color);
        int startX = position.x();
        int startY = position.y();

        int[] attackOffsets = {-1, 1}; // Attack left and right
        for (int xOffset : attackOffsets) {
            Position attackPos = new Position(startX + xOffset, startY + direction);
            if (GameUtils.isPositionOccupiedByOpponent(attackPos, color, chessBoard)){
                validMoves.add(attackPos);
            }
        }
    }

    private void tryToAddMove(GameStatusListener chessBoard, final Set<Position> validMoves, Position position, int directionY, int steps) {
        Position potentialPosition = new Position(position.x(), position.y() + (directionY * steps));
        if (GameUtils.isPositionEmpty(potentialPosition, chessBoard)) {
            validMoves.add(potentialPosition);
        }
    }

    private boolean isOnStartRow(Color color, int row) {
        return (color == Color.WHITE && row == WHITE_START_ROW) ||
                (color == Color.BLACK && row == BLACK_START_ROW);
    }

}
