package game.strategy;

import game.GameUtils;
import game.object.ChessGameState;
import game.object.ChessPiece;
import game.Position;

import java.util.ArrayList;
import java.util.List;

public class RookStrategy implements MoveStrategy {

    public RookStrategy() {
    }
    static final MoveCalculator straightMoveCalculator = (chessGameState, chessPiece, gameUtils) -> {
        List<Position> validMoves = new ArrayList<>();
        Position position = chessPiece.getPosition();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}; // 상, 하, 좌, 우 이동

        for (int[] direction : directions) {
            int x = position.getX();
            int y = position.getY();

            Position nextPosition = new Position(x + direction[0], y + direction[1]);
            while (gameUtils.isValidPosition(nextPosition)) {
                if (gameUtils.isPositionEmpty(nextPosition, chessGameState) ||
                        gameUtils.isPositionOccupiedByOpponent(nextPosition, chessPiece.getColor(), chessGameState)) {
                    validMoves.add(nextPosition);
                    if (gameUtils.isPositionOccupiedByOpponent(nextPosition, chessPiece.getColor(), chessGameState)) {
                        break; // 상대 말을 만나면 더 이상 진행하지 않음
                    }
                } else {
                    break; // 자기 말을 만나거나 유효하지 않은 위치일 경우 중단
                }
                // 다음 위치를 위해 새로운 Position 객체 생성
                nextPosition = new Position(nextPosition.getX() + direction[0], nextPosition.getY() + direction[1]);
            }
        }

        return validMoves;
    };
    @Override
    public List<Position> calculateMoves(ChessGameState chessGameState, ChessPiece chessPiece, GameUtils gameUtils) {
        return straightMoveCalculator.calculate(chessGameState, chessPiece, gameUtils);
    }
}
