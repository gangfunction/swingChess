package hello.move;

import hello.ChessBoard;
import hello.ChessPiece;
import hello.Player;
import hello.Position;

import java.util.ArrayList;
import java.util.List;

public class PawnStrategy implements MoveStrategy {

    @Override
    public List<Position> calculateMoves( ChessBoard chessBoard, ChessPiece piece) {
        List<Position> validMoves = new ArrayList<>();
        Position position = piece.getPosition();
        Player.Color color = piece.getColor();

        int direction = color == Player.Color.WHITE ? -1 : 1;
        int startX = position.getX();
        int startY = position.getY();

        // 직선 이동 검사
        Position oneStepForward = new Position(startX, startY + direction);
        if (chessBoard.isPositionEmpty(oneStepForward)) {
            validMoves.add(oneStepForward);

            // 첫 이동인 경우, 두 칸 이동 가능
            System.out.println("piece.getColor(): " + color);
            System.out.println("startY: " + startY);
            if ((color == Player.Color.WHITE && startY == 6) || (color == Player.Color.BLACK && startY == 1)) {
                System.out.println("startY: " + startY);
                Position twoStepsForward = new Position(startX, startY + 2 * direction);
                if (chessBoard.isPositionEmpty(twoStepsForward)) {
                    validMoves.add(twoStepsForward);
                }
            }
        }

        // 대각선 이동 검사 (상대 말을 잡을 수 있는 경우)
        Position[] attackPositions = {
                new Position(startX - 1, startY + direction),
                new Position(startX + 1, startY + direction)
        };
        for (Position attackPos : attackPositions) {
            if (chessBoard.isPositionOccupiedByOpponent(attackPos, color)){
                validMoves.add(attackPos);
            }
        }

        return validMoves;
    }

}
