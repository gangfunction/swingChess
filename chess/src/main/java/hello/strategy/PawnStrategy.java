package hello.strategy;

import hello.GameUtils;
import hello.Position;
import hello.core.Player;
import hello.gameobject.ChessGameState;
import hello.gameobject.ChessPiece;

import java.util.ArrayList;
import java.util.List;

public class PawnStrategy implements MoveStrategy {

    private static final int WHITE_START_ROW = 6;
    private static final int BLACK_START_ROW = 1;
    private static final int MOVE_ONE_STEP = 1;
    private static final int MOVE_TWO_STEPS = 2;


    public PawnStrategy() {
    }

    private int getMoveDirection(Player.Color color) {
        return (color == Player.Color.WHITE) ? -MOVE_ONE_STEP : MOVE_ONE_STEP;
    }
    @Override
    public List<Position> calculateMoves(ChessGameState chessBoard, ChessPiece piece, GameUtils utils) {
        List<Position> validMoves = new ArrayList<>();
        Position position = piece.getPosition();
        Player.Color color = piece.getColor();

        calculateStandardMoves(chessBoard, validMoves, position, color, utils);
        calculateAttackMoves(chessBoard, validMoves, position, color, utils);
        calculateEnPassantMoves(chessBoard, validMoves, position, color, utils);
        return validMoves;
    }

    private void calculateStandardMoves(ChessGameState chessBoard, final List<Position> validMoves, Position position, Player.Color color, GameUtils utils) {
        int direction = getMoveDirection(color);
        int startY = position.getY();

        // Check single step
        tryToAddMove(chessBoard, validMoves, position, direction, MOVE_ONE_STEP, utils);

        // Check double step for first move
        if (isOnStartRow(color, startY)) {
            tryToAddMove(chessBoard, validMoves, position, direction, MOVE_TWO_STEPS, utils);
        }
    }


    private void calculateEnPassantMoves(ChessGameState chessBoard, final List<Position> validMoves, Position position, Player.Color color, GameUtils utils) {
        // 앙파썽 조건 확인: 폰이 첫 이동으로 두 칸 전진했는지 확인

        int direction = (color == Player.Color.WHITE) ? -MOVE_ONE_STEP : +MOVE_ONE_STEP;
        int enPassantY = position.getY() + direction;
        int[] enPassantXs = {position.getX() - 1, position.getX() + 1};

        for (int enPassantX : enPassantXs) {
            if (enPassantX < 0 || enPassantX >= 8) continue; // 보드를 벗어나는 위치는 제외

            Position targetPawnPosition = new Position(enPassantX, position.getY());
            if (utils.isPositionOccupiedByOpponent(targetPawnPosition, color, chessBoard) &&
                    wasPawnMovedTwoSteps(position, chessBoard)) {
                Position enPassantPos = new Position(enPassantX, enPassantY); // 앙파썽 수행 후 폰이 도착할 위치
                validMoves.add(enPassantPos);
            }
        }
    }
    private boolean wasPawnMovedTwoSteps(Position targetPawnPosition, ChessGameState chessBoard) {
        ChessPiece lastMovedPawn = chessBoard.getLastMovedPiece();
        boolean lastMoveWasDoubleStep = chessBoard.getLastMoveWasDoubleStep();
        if (lastMovedPawn == null || !lastMoveWasDoubleStep) {
            return false;
        }
        Position lastMovedPosition = lastMovedPawn.getPosition();
        int currentX = targetPawnPosition.getX();
        int currentY = targetPawnPosition.getY();
        return (lastMovedPosition.getY() == currentY) && (Math.abs(lastMovedPosition.getX() - currentX) == 1);
    }

    private void calculateAttackMoves(ChessGameState chessBoard, final List<Position> validMoves, Position position, Player.Color color,GameUtils gameUtils) {
        int direction = getMoveDirection(color);
        int startX = position.getX();
        int startY = position.getY();

        int[] attackOffsets = {-1, 1}; // Attack left and right
        for (int xOffset : attackOffsets) {
            Position attackPos = new Position(startX + xOffset, startY + direction);
            if (gameUtils.isPositionOccupiedByOpponent(attackPos, color, chessBoard)){
                validMoves.add(attackPos);
            }
        }
    }

    private void tryToAddMove(ChessGameState chessBoard, final List<Position> validMoves, Position position, int directionY, int steps, GameUtils gameUtils) {
        Position potentialPosition = new Position(position.getX(), position.getY() + (directionY * steps));
        if (gameUtils.isPositionEmpty(potentialPosition, chessBoard)) {
            validMoves.add(potentialPosition);
        }
    }

    private boolean isOnStartRow(Player.Color color, int row) {
        return (color == Player.Color.WHITE && row == WHITE_START_ROW) ||
                (color == Player.Color.BLACK && row == BLACK_START_ROW);
    }

}
