package hello;

import hello.move.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ChessBoard {
    private List<Position> highlightedPositions = new ArrayList<>();
    private final JPanel boardPanel = new JPanel(new GridLayout(8, 8));
    private List<ChessPiece> chessPieces = new ArrayList<>();
    private ChessPiece selectedPiece = null;


    public ChessBoard() {
        initializeBoard();
        placeChessPieces();
    }

    private void initializeBoard() {
        for (int i = 0; i < 64; i++) {
            final int x = i % 8;
            final int y = i / 8;
            JPanel square = new JPanel(new GridBagLayout());
            square.setBackground((x + y) % 2 == 0 ? Color.WHITE : Color.GRAY);
            square.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleSquareClick(x, y);
                }
            });
            boardPanel.add(square);
        }
    }

    private void handleSquareClick(int x, int y) {
        Position clickedPosition = new Position(x, y);
        if (highlightedPositions.contains(clickedPosition) && selectedPiece != null) {
            // 선택된 말의 이전 위치를 저장
            Position oldPosition = selectedPiece.getPosition();
            System.out.println("oldPosition: " + oldPosition);

            // 말을 새 위치로 이동
            movePiece(selectedPiece, clickedPosition);

            // 선택된 말과 관련된 UI 업데이트
//            updatePieceUI(selectedPiece, oldPosition, clickedPosition);
            updateBoard();
            // 이동 후 보드와 하이라이트 상태 업데이트
            clearHighlights();
            highlightedPositions.clear();
            selectedPiece = null; // 선택 해제
        } else {
            Optional<ChessPiece> pieceOptional = findPieceAtPosition(x, y);
            if (pieceOptional.isPresent()) {
                ChessPiece clickedPiece = pieceOptional.get();
                if (selectedPiece == null || selectedPiece.getColor() == clickedPiece.getColor()) {
                    selectedPiece = clickedPiece;
                    highlightPossibleMoves(selectedPiece);
                } else {
                    JOptionPane.showMessageDialog(boardPanel, "상대방 말을 선택할 수 없습니다.");
                }
            } else {
                if (selectedPiece != null) {
                    JOptionPane.showMessageDialog(null, "올바른 요청이 아닙니다.");
                }
            }
        }

    }

    private Optional<ChessPiece> findPieceAtPosition(int x, int y) {
        return chessPieces.stream()
                .filter(piece -> piece.getPosition().getX() == x && piece.getPosition().getY() == y)
                .findFirst();
    }

    private void addPieceToBoard(ChessPiece.Type type, Player.Color color, int x, int y) {
        ChessPiece piece = new ChessPiece(type, new Position(x, y), color);
        chessPieces.add(piece); // 내부 리스트에 체스말 추가
        try {
            Icon icon = ChessPieceIconLoader.loadIcon(type, color);
            System.out.println(icon);

            JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
            JPanel panel = (JPanel) boardPanel.getComponent(y * 8 + x);
            panel.setLayout(new GridBagLayout()); // 아이콘을 가운데 정렬하기 위해 GridBagLayout 사용
            panel.add(pieceLabel);
            panel.validate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void highlightPossibleMoves(ChessPiece piece) {
        // 선택된 말에 대해 가능한 이동을 계산하고 하이라이트
        clearHighlights(); // 기존 하이라이트를 지웁니다.
        highlightedPositions.clear(); // 하이라이트된 위치 목록을 클리어합니다.

        List<Position> moves = calculateMovesForPiece(piece); // 이동 가능한 위치 계산
        highlightMoves(moves); // 하이라이트를 추가합니다.
        highlightedPositions.addAll(moves); // 하이라이트된 위치 목록에 추가합니다.
    }

    private List<Position> calculateMovesForPiece(ChessPiece piece) {
        ChessPiece.Type type = piece.getType();
        MoveStrategy moveStrategy = switch (type) {
            case PAWN -> new PawnStrategy();
            case KNIGHT -> new KnightStrategy();
            case BISHOP -> new BishopStrategy();
            case QUEEN -> new QueenStrategy();
            case ROOK -> new RookStrategy();
            case KING -> new KingStrategy();
        };
        return Objects.requireNonNull(moveStrategy).calculateMoves(this, piece);

    }

    private void placeChessPieces() {
        // 폰 배치
        for (int i = 0; i < 8; i++) {
            addPieceToBoard(ChessPiece.Type.PAWN, Player.Color.WHITE, i, 6);
            addPieceToBoard(ChessPiece.Type.PAWN, Player.Color.BLACK, i, 1);
        }
        // 룩 배치
        placePieceRow(0, Player.Color.BLACK);
        placePieceRow(7, Player.Color.WHITE);
    }

    private void placePieceRow(int y, Player.Color color) {
        addPieceToBoard(ChessPiece.Type.ROOK, color, 0, y);
        addPieceToBoard(ChessPiece.Type.KNIGHT, color, 1, y);
        addPieceToBoard(ChessPiece.Type.BISHOP, color, 2, y);
        addPieceToBoard(ChessPiece.Type.QUEEN, color, 3, y);
        addPieceToBoard(ChessPiece.Type.KING, color, 4, y);
        addPieceToBoard(ChessPiece.Type.BISHOP, color, 5, y);
        addPieceToBoard(ChessPiece.Type.KNIGHT, color, 6, y);
        addPieceToBoard(ChessPiece.Type.ROOK, color, 7, y);
    }

    public JPanel getBoardPanel() {
        return boardPanel;
    }

    public List<ChessPiece> getChessPieces() {
        return chessPieces;
    }

    public void resetBoard() {
        // 기존 체스말 제거
        chessPieces.clear();
        boardPanel.removeAll();

        // 보드 배경 초기화
        initializeBoard();

        // 체스말 재배치
        placeChessPieces();

        // UI 갱신
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public boolean isPositionEmpty(Position position) {
        return chessPieces.stream()
                .noneMatch(piece -> piece.getPosition().equals(position));
    }

    public boolean isPositionOccupiedByOpponent(Position position, Player.Color currentPlayerColor) {
        return chessPieces.stream()
                .anyMatch(piece -> piece.getPosition().equals(position) && piece.getColor() != currentPlayerColor);
    }

    public void highlightMoves(List<Position> moves) {
        clearHighlights(); // 기존 하이라이트 제거

        for (Position move : moves) {
            int index = move.getY() * 8 + move.getX(); // 1차원 배열에서의 위치 계산
            JPanel square = (JPanel) boardPanel.getComponent(index);
            square.setBackground(Color.YELLOW); // 하이라이트 색상 설정
        }
    }

    private void movePiece(ChessPiece piece, Position newPosition) {
        // 이동하려는 위치에 상대방의 말이 있는지 확인하고 제거합니다.
        Optional<ChessPiece> opponentPiece = findPieceAtPosition(newPosition.getX(), newPosition.getY());
        opponentPiece.ifPresent(chessPieces::remove); // 상대방 말이 있으면 체스판에서 제거합니다.

        // 기존 위치에서 선택된 말을 제거하지 않고, 단순히 위치만 업데이트합니다.
        // (체스 말 리스트에서 실제로 제거하지 않습니다. 위치만 변경합니다.)
        piece.setPosition(newPosition); // 말의 위치를 업데이트합니다.

        // 체스판 UI를 업데이트합니다.
        updateBoard();

    }

    public void clearHighlights() {
        for (int i = 0; i < 64; i++) {
            JPanel square = (JPanel) boardPanel.getComponent(i);
            Color background = (i / 8 + i % 8) % 2 == 0 ? Color.WHITE : Color.GRAY; // 원래 색상 복원
            square.setBackground(background);
        }
    }


    private void updateBoard() {
        // 먼저 모든 패널을 클리어한 후 현재 게임 상태를 반영하여 말들을 다시 추가합니다.
        // 이 접근 방식은 개별 말의 UI를 업데이트하기 위한 별도의 메서드가 필요 없게 만듭니다.
        for (Component comp : boardPanel.getComponents()) {
            JPanel panel = (JPanel) comp;
            panel.removeAll(); // 패널을 클리어하여 말의 업데이트를 준비합니다.
        }

        // 모든 체스 말들을 순회하며 현재 위치에 맞게 보드에 배치합니다.
        for (ChessPiece piece : chessPieces) {
            int index = piece.getPosition().getY() * 8 + piece.getPosition().getX();
            JPanel panel = (JPanel) boardPanel.getComponent(index);
            Icon icon = ChessPieceIconLoader.loadIcon(piece.getType(), piece.getColor());
            JLabel label = new JLabel(icon);
            panel.add(label); // 패널에 말 아이콘을 추가합니다.
        }

        // 변경 사항을 반영하여 보드 UI를 새로고침합니다.
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public boolean isValidPosition(Position position) {
        int x = position.getX();
        int y = position.getY();
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }
}
