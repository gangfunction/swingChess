# Chess Game
## 1. Game Introduction
The chess game, which is commonly known, has been implemented using a Swing GUI.

## 2. Installation Method
### 2.1 Execution Environment
- Java 17 or higher

## 3. Game Development Status
### 3.1 Component Implementation
- [x] Chessboard implementation
- [x] Piece implementation
- [x] Piece movement implementation
- [x] Castling implementation
    - [x] Kingside castling implementation
    - [x] Queenside castling implementation
- [x] Pawn promotion implementation
- [x] En passant implementation

### 3.2 Game End Conditions Implementation
- [x] Victory conditions implementation
    - [x] Checkmate implementation
    - [x] Check implementation
- [ ] Draw conditions implementation
    - [x] Stalemate implementation
    - [x] 50-move rule implementation
    - [x] Threefold repetition rule implementation
    - [ ] Draw by agreement implementation

### 3.3 Log Implementation
- [x] Turn display log implementation

### 3.4 Game Save and Load Implementation
- [x] Game save implementation
    - [x] Game serialization implementation
    - [x] Save file name with UTC time implementation
- [x] Game load implementation
    - [x] Game deserialization implementation
    - [x] Load game by file name implementation

### 3.5 Additional Features Implementation
- [x] Game start screen implementation
- [x] Undo implementation
- [x] Redo implementation
- [x] Game end screen implementation

### 4. Computer Match Implementation
- [x] Integration with open-source chess engine (Stockfish)
- [ ] Improvement of serialization state and logging method (planned to use FEN (Forsyth-Edwards Notation))
- [ ] Implementation of rating setting function for computer matches

---------

# 체스 게임(Chess Game)
## 1. 게임소개
일반적으로 알고있던 체스게임을 스윙 GUI를 통하여 구현했습니다.
## 2. 설치방법
### 2.1. 실행환경
- Java 17 이상
## 3. 게임개발 현황
### 3.1 구성요소 구현
- [x] 체스판 구현
- [x] 말 구현
- [x] 말 이동 구현
- [x] 캐슬링 구현
  - [x] 킹사이드 캐슬링 구현
  - [x] 퀸사이드 캐슬링 구현
- [x] 폰 프로모션 구현
- [x] 앙파상 구현

### 3.2 게임종료 조건 구현
- [x] 승리 조건 구현
  - [x] 체크메이트 구현
  - [x] 체크 구현
- [ ] 무승부 조건 구현
  - [x] 스테일메이트 구현
  - [x] 50수 룰 구현
  - [x] 3회 반복 룰 구현
  - [ ] 무승부 동의 구현

### 3.3 로그 구현
- [x] 차례 표시 로그 구현
### 3.4 게임 저장 및 불러오기 구현
- [x] 게임 저장 구현
  - [x] 게임 직렬화 구현
  - [x] utc 시간으로 파일명 저장 구현
- [x] 게임 불러오기 구현
  - [x] 게임 역직렬화 구현
  - [x] 파일명으로 게임 불러오기 구현

### 3.5 부가 요소 구현
- [x] 게임 시작 화면 구현
- [x] undo 구현
- [x] redo 구현
- [x] 게임 종료 화면 구현

### 4. 컴퓨터 대전 구현
- [x] 오픈소스 체스 엔진 연동 (Stockfish)
- [ ] 직렬화 상태 및 로그방법의 개선 (FEN(Forsyth-Edwards Notation) 사용 예정)
- [ ] 컴퓨터 대전시 레이팅 설정 기능 구현