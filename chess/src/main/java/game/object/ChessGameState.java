package game.object;

import game.Position;
import game.core.Color;
import game.factory.ChessPiece;
import game.factory.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChessGameState implements GameStatusListener {

    private final List<ChessPiece> chessPieces = new ArrayList<>();
    private ChessPiece selectedPiece = null;
    private ChessPiece lastMovedPiece = null; // 마지막으로 이동한 폰
    private boolean lastMoveWasDoubleStep = false; // 마지막 이동이 두 칸이었는지 여부
    private boolean canCastle = false;
    private GameLogicActions gameLogicActions;
    public void setGameLogicActions(GameLogicActions gameLogicActions){
        this.gameLogicActions = gameLogicActions;
    }


    public ChessGameState() {
    }

    @Override
    public void addChessPiece(ChessPiece chessPiece) {
        chessPieces.add(chessPiece);
    }

    @Override
    public List<ChessPiece> getChessPieces() {
        return new ArrayList<>(chessPieces);
    }

    @Override
    public void clearChessPieces() {
        chessPieces.clear();
    }


    @Override
    public ChessPiece getSelectedPiece() {
        return selectedPiece;
    }

    @Override
    public void setSelectedPiece(ChessPiece piece){
        this.selectedPiece = piece;
    }

    // 필요에 따라 추가 메서드 구현
    @Override
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

    @Override
    public Optional<ChessPiece> getChessPieceAt(Position targetPosition) {
        for (ChessPiece piece : chessPieces) {
            if (piece.getPosition().equals(targetPosition)) {
                return Optional.of(piece);
            }
        }
        return Optional.empty();
    }
    @Override
    public ChessPiece getLastMovedPiece(){
        return this.lastMovedPiece;
    }
    @Override
    public boolean getLastMoveWasDoubleStep(){
        return this.lastMoveWasDoubleStep;
    }

    @Override
    public void removeChessPiece(ChessPiece targetPawn) {
        chessPieces.remove(targetPawn);
    }
    @Override
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


    private int moveWithoutPawnOrCaptureCount = 0;
    @Override
    public void updateMoveWithoutPawnOrCaptureCount(boolean isPawnMove, boolean isCapture) {
        if (isPawnMove || isCapture) {
            moveWithoutPawnOrCaptureCount = 0;
        } else {
            moveWithoutPawnOrCaptureCount++;
        }
    }

    @Override
    public int getMoveWithoutPawnOrCaptureCount() {
        return moveWithoutPawnOrCaptureCount;
    }

    @Override
    public boolean isAvailableMoveTarget(Position position, ChessGameLogic chessGameLogic) {
        ChessPiece selectedPiece = getSelectedPiece();
        System.out.println("selectedPiece:" + selectedPiece.getPosition().x() + " " + selectedPiece.getPosition().y());
        List<Position> validMoves = chessGameLogic.calculateMovesForPiece(selectedPiece);
        if(canCastle){
            validMoves.add(new Position(2, selectedPiece.getPosition().y()));
            validMoves.add(new Position(6, selectedPiece.getPosition().y()));
            gameLogicActions.setAfterCastling(true);
        }

        return validMoves.contains(position) && !chessGameLogic.isFriendlyPieceAtPosition(position, selectedPiece);
    }

    @Override
    public ChessPiece getKing(Color color) {
        for (ChessPiece piece : chessPieces) {
            if (piece.getType() == Type.KING && piece.getColor() == color) {
                return piece;
            }
        }
        return null;
    }

    @Override
    public Position getEnPassantTarget() {
        return null;
    }

    @Override
    public char[] getCastlingRights() {
        return new char[0];
    }

    @Override
    public void setCanCastle(boolean b) {
        this.canCastle = true;
    }
}
