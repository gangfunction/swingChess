package hello.move;

import hello.ChessBoard;
import hello.ChessPiece;
import hello.Player;
import hello.Position;

import java.util.ArrayList;
import java.util.List;

public class Pawn {
    public List<Position> calculatePawnMoves(ChessPiece piece, ChessBoard chessBoard) {
        List<Position> validMoves = new ArrayList<>();
        int direction = piece.getColor() == Player.Color.WHITE ? -1 : 1;
        int startX = piece.getPosition().getX();
        int startY = piece.getPosition().getY();

        // 직선 이동 검사
        Position oneStepForward = new Position(startX, startY + direction);
        if (chessBoard.isPositionEmpty(oneStepForward)) {
            validMoves.add(oneStepForward);

            // 첫 이동인 경우, 두 칸 이동 가능
            System.out.println("piece.getColor(): " + piece.getColor());
            System.out.println("startY: " + startY);
            if ((piece.getColor() == Player.Color.WHITE && startY == 6) || (piece.getColor() == Player.Color.BLACK && startY == 1)) {
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
            if (chessBoard.isPositionOccupiedByOpponent(attackPos, piece.getColor())){
                validMoves.add(attackPos);
            }
        }

        return validMoves;
    }

}
