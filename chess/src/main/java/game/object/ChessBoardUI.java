package game.object;

import game.GameEventListener;
import game.Position;
import game.core.Player;
import game.factory.*;
import game.ui.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class ChessBoardUI implements GameEventListener, SquareClickListener {
    private final ChessGameState chessGameState;
    private JPanel boardPanel = new JPanel(new GridLayout(8, 8));
    private static final int BOARD_SIZE = 8;
    private static final Color LIGHT_SQUARE_COLOR = Color.WHITE;
    private static final Color DARK_SQUARE_COLOR = Color.GRAY;

    private ChessGameLogic chessGameLogic;

    public ChessBoardUI(ChessGameState chessGameState, ChessGameLogic chessGameLogic) {
        this.chessGameLogic = chessGameLogic;
        this.chessGameState = chessGameState;
        initializeBoard();
        placeChessPieces();
    }

    public void highlightMoves(List<Position> moves, ChessBoardUI chessBoardUI) {
        clearHighlights(chessBoardUI);
        for (Position move : moves) {
            System.out.println("highlighting move: " + move);
            JPanel square = getPanelAtPosition(move);
            square.setBackground(Color.YELLOW);
        }
    }

    public void refreshPanel(JPanel panel, Icon icon) {
        clearPanel(panel);
        if (icon != null) {
            JLabel label = new JLabel(icon);
            panel.add(label);
        }
        panel.revalidate();
        panel.repaint();
    }


    private static final Set<Position> highlightedPositions = new HashSet<>();

    public static Set<Position> getHighlightedPositions() {
        return highlightedPositions;
    }

    public void highlightPossibleMoves(ChessPiece piece, ChessGameLogic chessGameLogic, ChessBoardUI chessBoardUI) {
        // 선택된 말에 대해 가능한 이동을 계산하고 하이라이트
        clearHighlights(chessBoardUI); // 기존 하이라이트를 지웁니다.
        getHighlightedPositions().clear(); // 하이라이트된 위치 목록을 클리어합니다.
        List<Position> moves = chessGameLogic.calculateMovesForPiece(piece); // 이동 가능한 위치 계산
        highlightMoves(moves, chessBoardUI); // 하이라이트를 추가합니다.
        highlightedPositions.addAll(moves); // 하이라이트된 위치 목록에 추가합니다.
    }

    public void clearHighlights(ChessBoardUI chessBoardUI) {
        if (chessBoardUI == null || chessBoardUI.getBoardPanel() == null) {
            return;
        }
        for (int i = 0; i < 64; i++) {
            JPanel square = (JPanel) chessBoardUI.getBoardPanel().getComponent(i);
            if (square != null) {
                chessBoardUI.setDefaultTileBackground(i, square);
            }
        }
    }

    public void clearPanel(JPanel panel) {
        panel.removeAll();
        panel.revalidate();
        panel.repaint();
    }

    public void showError(ChessGameState chessGameState, ChessBoardUI chessBoardUI) {
        if (chessGameState.getSelectedPiece() == null) {
            JOptionPane.showMessageDialog(null, "Invalid move. Try again.");
        } else {
            JOptionPane.showMessageDialog(chessBoardUI.getBoardPanel(), "Cannot select opponent's piece.");
        }
    }

    public void tempPlacePieceOnBoard(ChessPiece piece) {
        JPanel panel = getPanelAtPosition(piece.getPosition());
        Icon icon = IconLoader.loadIcon(piece.getType(), piece.getColor());
        refreshPanel(panel, icon);
    }

    void updateBoard(ChessGameState chessGameState, ChessBoardUI chessBoardUI) {
        clearBoard(chessBoardUI);
        chessGameState.getChessPieces().forEach(this::tempPlacePieceOnBoard);
    }

    private void clearBoard(ChessBoardUI chessBoardUI) {
        for (int i = 0; i < 64; i++) {
            JPanel panel = (JPanel) chessBoardUI.getBoardPanel().getComponent(i);
            clearPanel(panel);
        }
    }

    public JPanel getPanelAtPosition(Position position) {

        int index = position.getY() * 8 + position.getX();
        if (index < 0 || index >= boardPanel.getComponentCount()) {
            throw new IllegalArgumentException("Position is out of board's bounds.");
        }

        Component comp = boardPanel.getComponent(index);
        if (comp instanceof JPanel) {
            return (JPanel) comp;
        } else {
            throw new IllegalStateException("Expected JPanel at the given position but found another component type.");
        }
    }


    public JPanel getBoardPanel() {
        return boardPanel;
    }

    JPanel createSquare(int index) {
        final int x = index % BOARD_SIZE;
        final int y = index / BOARD_SIZE;
        JPanel square = new JPanel(new GridBagLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(getWidth() / 8, getHeight() / 8);
            }
        };
        square.setBackground((x + y) % 2 == 0 ? LIGHT_SQUARE_COLOR : DARK_SQUARE_COLOR);
        square.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("x: " + x + ", y: " + y);
                chessGameLogic.handleSquareClick(x, y);
            }
        });
        return square;
    }

    void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            JPanel square = createSquare(i);
            boardPanel.add(square);
        }
    }

    public void clearSquare(Position position) {
        int index = position.getY() * BOARD_SIZE + position.getX();
        JPanel panel = (JPanel) getBoardPanel().getComponent(index);
        panel.removeAll();
        panel.revalidate();
        panel.repaint();
    }

    public void setDefaultTileBackground(int index, JPanel panel) {
        Color backgroundColor = ((index / BOARD_SIZE) + (index % BOARD_SIZE)) % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY;
        panel.setBackground(backgroundColor);
    }


    public void resetBoard() {
        // 기존 체스말 제거
        chessGameState.getChessPieces().clear();
        boardPanel.removeAll();

        // 보드 배경 초기화
        initializeBoard();

        // 체스말 재배치
        placeChessPieces();

        // UI 갱신
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    void placeChessPieces() {
// 기본 배치를 위한 행 - 폰들과 다른 말들
        IntStream.range(0, BOARD_SIZE).forEach(i -> {
            placePieceOnboard(PawnFactory.getInstance().createChessPiece(new Position(i, 6), Player.Color.WHITE));
            placePieceOnboard(PawnFactory.getInstance().createChessPiece(new Position(i, 1), Player.Color.BLACK));
        });

        // 특수한 말 배치
        placePieceRow(0, Player.Color.BLACK);
        placePieceRow(7, Player.Color.WHITE);
    }

    void placePieceRow(int y, Player.Color color) {
        // 각 (룩, 나이트, 비숍, 퀸, 킹, 비숍, 나이트, 룩) 위치에 배치
        placePieceOnboard(RookFactory.getInstance().createChessPiece(new Position(0, y), color));
        placePieceOnboard(KnightFactory.getInstance().createChessPiece(new Position(1, y), color));
        placePieceOnboard(BishopFactory.getInstance().createChessPiece(new Position(2, y), color));
        placePieceOnboard(QueenFactory.getInstance().createChessPiece(new Position(3, y), color));
        placePieceOnboard(KingFactory.getInstance().createChessPiece(new Position(4, y), color));
        placePieceOnboard(BishopFactory.getInstance().createChessPiece(new Position(5, y), color));
        placePieceOnboard(KnightFactory.getInstance().createChessPiece(new Position(6, y), color));
        placePieceOnboard(RookFactory.getInstance().createChessPiece(new Position(7, y), color));
    }


    // 추가 UI 메서드 구현
    public void placePieceOnboard(ChessPiece chessPiece) {
        chessGameState.getChessPieces().add(chessPiece); // 내부 리스트에 체스말 추가
        try {
            Icon icon = IconLoader.loadIcon(chessPiece.getType(), chessPiece.getColor());
            JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
            JPanel panel = (JPanel) boardPanel.getComponent(chessPiece.getPosition().getY() * 8 + chessPiece.getPosition().getX());
            panel.setLayout(new GridBagLayout());
            panel.add(pieceLabel);
            panel.validate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPieceMoved(Position oldPosition, Position newPosition, ChessPiece piece) {
        int oldIndex = oldPosition.getY() * 8 + oldPosition.getX();
        int newIndex = newPosition.getY() * 8 + newPosition.getX();

        JPanel oldPanel = getPanelAtPosition(oldPosition);
        // 기존 위치의 패널 클리어
        oldPanel.removeAll();
        oldPanel.revalidate();
        oldPanel.repaint();
        setDefaultTileBackground(oldIndex, oldPanel); // 체스판의 체스 타일 배경을 원래대로 복구하는 메서드

        JPanel newPanel = (JPanel) getBoardPanel().getComponent(newIndex);
        Icon icon = IconLoader.loadIcon(piece.getType(), piece.getColor());
        newPanel.add(new JLabel(icon));
        // UI 갱신
        oldPanel.revalidate();
        oldPanel.repaint();
        // 새 위치에 기물 배치
        newPanel.revalidate();
        newPanel.repaint();
    }

    @Override
    public void onPieceSelected(ChessPiece piece) {

    }

    @Override
    public void onInvalidMoveAttempted() {

    }


    @Override
    public void onSquareClick(int x, int y) {

    }
}
