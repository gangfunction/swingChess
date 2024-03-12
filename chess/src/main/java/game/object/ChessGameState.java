package game.object;

import game.Position;
import game.core.Player;

import java.util.ArrayList;
import java.util.List;

public class ChessGameState {

    private final List<ChessPiece> chessPieces = new ArrayList<>();
    private ChessPiece selectedPiece = null;
    private ChessPiece lastMovedPiece = null; // 마지막으로 이동한 폰
    public boolean lastMoveWasDoubleStep = false; // 마지막 이동이 두 칸이었는지 여부
    private Player lastPlayer;

    public ChessGameState() {
    }

    public void addChessPiece(ChessPiece chessPiece) {
        chessPieces.add(chessPiece);
    }

    public List<ChessPiece> getChessPieces() {
        return chessPieces;
    }

    public void clearChessPieces() {
        chessPieces.clear();
    }


    public ChessPiece getSelectedPiece() {
        return selectedPiece;
    }

    public void setSelectedPiece(ChessPiece piece){
        this.selectedPiece = piece;
    }

    // 필요에 따라 추가 메서드 구현
    public void updateLastMovedPawn(ChessPiece pawn, Position from, Position to) {
        // 폰의 이동 거리 계산
        int distanceMoved = Math.abs(from.getY() - to.getY());

        // 이동한 거리가 2칸인 경우만 업데이트
        if (distanceMoved == 2) {
            this.lastMovedPiece = pawn;
            this.lastMoveWasDoubleStep = true;
        } else {
            // 이동 거리가 2칸이 아니면 앙파썽 관련 정보 초기화
            this.lastMovedPiece = null;
            this.lastMoveWasDoubleStep = false;
        }
    }
    public void updateLastMoveInfo(ChessPiece piece, Position from, Position to, Player currntPlayer){
        this.lastMovedPiece = piece;
        this.lastMoveWasDoubleStep = Math.abs(from.getY() - to.getY()) == 2;
        this.lastPlayer = currntPlayer;
    }
//    public boolean isEnPassantPossible(Position attackingPawnPos, Position targetPawnPos) {
//        // 마지막 이동한 폰이 앙파썽 대상 폰인지 확인
//        System.out.println("앙파썽 대상인가요?");
//        if (lastMoveWasDoubleStep && lastMovedPiece != null && lastPlayer != currentPlayer) {
//            System.out.println("앙파썽 대상입니다.");
//            return lastMovedPiece.getPosition().equals(targetPawnPos) &&
//                    // 앙파썽을 시도하는 폰의 위치가 마지막으로 이동한 폰의 옆 칸인지 확인
//                    Math.abs(lastMovedPiece.getPosition().getX() - attackingPawnPos.getX()) == 1;
//        }
//        return false;
//    }
    public boolean isEnPassantPossible(Position attackingPawnPos, Player currentPlayer) {
        if (lastMoveWasDoubleStep && lastMovedPiece != null && lastPlayer != currentPlayer) {
            // 앙파썽 대상 폰이 바로 이전 턴에 이동했는지 확인
            return true;
        }
        return false;
    }

    public ChessPiece getChessPieceAt(Position targetPosition) {
        for (ChessPiece piece : chessPieces) {
            if (piece.getPosition().equals(targetPosition)) {
                return piece;
            }
        }
        return null;
    }
    public ChessPiece getLastMovedPiece(){
        return this.lastMovedPiece;
    }
    public boolean getLastMoveWasDoubleStep(){
        return this.lastMoveWasDoubleStep;
    }

    public void removeChessPiece(ChessPiece targetPawn) {
        chessPieces.remove(targetPawn);
    }

}
