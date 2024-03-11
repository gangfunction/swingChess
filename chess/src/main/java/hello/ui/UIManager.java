package hello.ui;

import hello.gameobject.ChessBoard;
import hello.gameobject.ChessPiece;
import hello.Position;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class UIManager {
    private final ChessBoard chessBoard;

    public UIManager(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    public void showError() {
        if (chessBoard.getSelectedPiece() == null) {
            JOptionPane.showMessageDialog(null, "Invalid move. Try again.");
        } else {
            JOptionPane.showMessageDialog(chessBoard.getBoardPanel(), "Cannot select opponent's piece.");
        }
    }

    public void highlightMoves(List<Position> moves) {
        chessBoard.getUIManager().clearHighlights(chessBoard); // 기존 하이라이트 제거
        for (Position move : moves) {
            int index = move.getY() * 8 + move.getX(); // 1차원 배열에서의 위치 계산
            JPanel square = (JPanel) chessBoard.getBoardPanel().getComponent(index);
            square.setBackground(Color.YELLOW); // 하이라이트 색상 설정
        }
    }

    void updateBoard() {
        // 먼저 모든 패널을 클리어한 후 현재 게임 상태를 반영하여 말들을 다시 추가합니다.
        // 이 접근 방식은 개별 말의 UI를 업데이트하기 위한 별도의 메서드가 필요 없게 만듭니다.
        for (Component comp : chessBoard.getBoardPanel().getComponents()) {
            JPanel panel = (JPanel) comp;
            panel.removeAll(); // 패널을 클리어하여 말의 업데이트를 준비합니다.
        }

        // 모든 체스 말들을 순회하며 현재 위치에 맞게 보드에 배치합니다.
        for (ChessPiece piece : chessBoard.getChessPieces()) {
            int index = piece.getPosition().getY() * 8 + piece.getPosition().getX();
            JPanel panel = (JPanel) chessBoard.getBoardPanel().getComponent(index);
            Icon icon = IconLoader.loadIcon(piece.getType(), piece.getColor());
            JLabel label = new JLabel(icon);
            panel.add(label); // 패널에 말 아이콘을 추가합니다.
        }

        // 변경 사항을 반영하여 보드 UI를 새로고침합니다.
        chessBoard.getBoardPanel().revalidate();
        chessBoard.getBoardPanel().repaint();
    }

    public void clearHighlights(ChessBoard chessBoard) {
        for (int i = 0; i < 64; i++) {
            JPanel square = (JPanel) chessBoard.getBoardPanel().getComponent(i);
            Color background = (i / 8 + i % 8) % 2 == 0 ? Color.WHITE : Color.GRAY; // 원래 색상 복원
            square.setBackground(background);
        }
    }


    public void updateUIAfterMove(Position oldPosition, Position newPosition, ChessPiece piece) {
        int oldIndex = oldPosition.getY() * 8 + oldPosition.getX();
        int newIndex = newPosition.getY() * 8 + newPosition.getX();

        // 이전 위치의 JPanel을 빈 공간으로 처리
        JPanel oldPanel = (JPanel) chessBoard.getBoardPanel().getComponent(oldIndex);
        oldPanel.removeAll();
        chessBoard.setDefaultTileBackground(oldIndex, oldPanel); // 체스판의 체스 타일 배경을 원래대로 복구하는 메서드


        JPanel newPanel = (JPanel) chessBoard.getBoardPanel().getComponent(newIndex);
        Icon icon = IconLoader.loadIcon(piece.getType(), piece.getColor());
        newPanel.add(new JLabel(icon));
        // UI 갱신
        oldPanel.revalidate();
        oldPanel.repaint();
        // 새 위치에 기물 배치
        newPanel.revalidate();
        newPanel.repaint();

    }
}