package hello;

import hello.move.Knight;
import hello.move.Pawn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChessBoard {
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
        //일단 클릭한 위치에 말이 있는지 확인
        Optional<ChessPiece> pieceOptional = findPieceAtPosition(x, y);
        if (pieceOptional.isPresent()) {
            selectedPiece = pieceOptional.get();
            //TODO: 선택된 말이 이동가능한 거리를 타입에 따라 계산한다.
            switch (selectedPiece.getType()) {
                case PAWN:
                    Pawn pawn = new Pawn();
                    List<Position> moves = pawn.calculatePawnMoves(selectedPiece, this);
                    highlightMoves(moves);
                    break;
                case KNIGHT:
                    Knight knight = new Knight();
                    List<Position> moves1 = knight.calculateKnightMoves(selectedPiece, this);
                    highlightMoves(moves1);
                    break;
            }


            // 선택된 말에 대한 로직을 여기에 추가
            System.out.println("Selected piece at: " + x + ", " + y);
        } else {
            if (selectedPiece != null) {
                // 선택된 말을 (x, y)로 이동하는 로직
                System.out.println("Move piece to: " + x + ", " + y);
                selectedPiece = null; // 선택 해제
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

        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
        for (Position move : moves) {
            int index = move.getY() * 8 + move.getX(); // 1차원 배열에서의 위치 계산
            JPanel square = (JPanel) boardPanel.getComponent(index);
            square.setBackground(Color.YELLOW); // 하이라이트 색상 설정
        }
    }

    public void clearHighlights() {
        for (int i = 0; i < 64; i++) {
            JPanel square = (JPanel) boardPanel.getComponent(i);
            Color background = (i / 8 + i % 8) % 2 == 0 ? Color.WHITE : Color.GRAY; // 원래 색상 복원
            square.setBackground(background);
        }
    }
}
