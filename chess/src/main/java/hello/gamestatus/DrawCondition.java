package hello.gamestatus;


public class DrawCondition {


    public boolean isStalemate() {
        // 여기에 스테일메이트 판정 로직 구현
        return false; // 적절한 로직으로 대체
    }

    public boolean isInsufficientMaterial() {
        // 여기에 인성규칙 판정 로직 구현
        // 대략적인 예시: 왕과 기사, 또는 왕과 비숍만 남은 경우는 무승부로 판정할 수 있습니다.
//        Set<ChessPiece> pieces = chessBoard.getPieceSet();
        // pieces를 검사하여 무승부가 확실한 조합인지 확인하는 로직
        return false; // 적절한 로직으로 대체
    }

    public boolean isThreefoldRepetition() {
        // 여기에 동일한 위치 반복 판정 로직 구현
        // 상태 기록을 통해 같은 위치가 세 번 반복되었는지 체크
        return false; // 적절한 로직으로 대체
    }

    public boolean isFiftyMoveRule() {
        // 여기에 오십 수 규칙 판정 로직 구현
        // 50회 이동 동안 폰 이동이 없고 말이 잡히지 않았는지를 체크
        return false; // 적절한 로직으로 대체
    }

    // 무승부 조건들을 종합하고, 현재 체스 게임이 무승부인지 판단하는 메서드
    public boolean isDraw() {
        return isStalemate() || isInsufficientMaterial() || isThreefoldRepetition() || isFiftyMoveRule();
    }
}
