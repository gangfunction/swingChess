package game.object;

import game.GameUtils;
import game.Position;
import game.core.Color;
import game.factory.ChessPiece;
import game.factory.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//TODO: 캐슬링 상태 업데이트
public class ChessGameState {

    private final List<ChessPiece> chessPieces = new ArrayList<>();
    private ChessPiece selectedPiece = null;
    private ChessPiece lastMovedPiece = null; // 마지막으로 이동한 폰
    public boolean lastMoveWasDoubleStep = false; // 마지막 이동이 두 칸이었는지 여부

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
        int distanceMoved = Math.abs(from.y() - to.y());

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

    public Optional<ChessPiece> getChessPieceAt(Position targetPosition) {
        for (ChessPiece piece : chessPieces) {
            if (piece.getPosition().equals(targetPosition)) {
                return Optional.of(piece);
            }
        }
        return Optional.empty();
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
    public boolean isRookUnmovedForCastling(Color color, Position kingPosition) {
        // 캐슬링이 가능한 룩의 위치를 확인
        Position rookPosition = kingPosition.x() == 2 ?
                new Position(0, kingPosition.y()) : // 퀸 사이드 캐슬링
                new Position(7, kingPosition.y());  // 킹 사이드 캐슬링

        Optional<ChessPiece> rook = getChessPieceAt(rookPosition);
        if(rook.isPresent() && rook.get().getType() == Type.ROOK && rook.get().getColor() == color){
            return !rook.get().isMoved();
        }
        return false; // 해당 위치에 룩이 없거나 이미 이동했으면 false 반환
    }

    public void movePiece(ChessPiece piece, Position moveTo) {
        getChessPieceAt(moveTo).ifPresent(this::removeChessPiece);
        piece.setPosition(moveTo);
        piece.setMoved(true);
    }


    private int moveWithoutPawnOrCaptureCount = 0;
    public void updateMoveWithoutPawnOrCaptureCount(boolean isPawnMove, boolean isCapture) {
        if (isPawnMove || isCapture) {
            moveWithoutPawnOrCaptureCount = 0;
        } else {
            moveWithoutPawnOrCaptureCount++;
        }
    }

    public int getMoveWithoutPawnOrCaptureCount() {
        return moveWithoutPawnOrCaptureCount;
    }

    boolean isAvailableMoveTarget(Position position, ChessGameLogic chessGameLogic) {
        ChessPiece selectedPiece = getSelectedPiece();
        System.out.println("selectedPiece:" + selectedPiece.getPosition().x() + " " + selectedPiece.getPosition().y());
        List<Position> validMoves = chessGameLogic.calculateMovesForPiece(selectedPiece);
        for (Position p : validMoves) {
            System.out.println("validMoves:" + p.x() + " " + p.y());
        }

        return validMoves.contains(position) && !chessGameLogic.isFriendlyPieceAtPosition(position, selectedPiece);
    }
}
