package game.object;

import game.GameUtils;
import game.Position;
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

public class ChessBoardUI implements GameEventListener {
    private final JPanel boardPanel = new JPanel(new GridLayout(8, 8));
    private final ChessGameState chessGameState;
    private static final int BOARD_SIZE = 8;
    private GameLogicActions gameLogicActions;
    private static final Color LIGHT_SQUARE_COLOR = Color.WHITE;
    private static final Color DARK_SQUARE_COLOR = Color.GRAY;

    public void setGameLogicActions(GameLogicActions gameLogicActions) {
        this.gameLogicActions = gameLogicActions;
    }


    public ChessBoardUI(ChessGameState chessGameState) {
        this.chessGameState = chessGameState;
        initializeBoard();
        placeChessPieces();
    }
    public void highlightMoves(List<Position> moves) {
        clearHighlights();
        moves.forEach(this::highlightSingleMove);
    }

    public void highlightSingleMove(Position position) {
        JPanel square = getPanelAtPosition(position);
        square.setBackground(Color.YELLOW);
    }

    private static final Set<Position> highlightedPositions = new HashSet<>();


    public static Set<Position> getHighlightedPositions() {
        return highlightedPositions;
    }

    public void highlightPossibleMoves(ChessPiece piece) {
        clearHighlights();
        getHighlightedPositions().clear();
        List<Position> moves = piece.calculateMoves(chessGameState,new GameUtils()) ;// 이동 가능한 위치 계산
        highlightMoves(moves);
        highlightedPositions.addAll(moves); // 하이라이트된 위치 목록에 추가합니다.
    }

    public void clearHighlights() {
        if (getBoardPanel() == null) {
            return;
        }
        for (int i = 0; i < BOARD_SIZE*BOARD_SIZE; i++) {
            JPanel square = (JPanel) getBoardPanel().getComponent(i);
            if (square != null) {
                setDefaultTileBackground(i, square);
            }
        }
    }

    public JPanel getPanelAtPosition(Position position) {

        int index = position.y() * BOARD_SIZE + position.x();
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
                if(gameLogicActions != null){
                    gameLogicActions.handleSquareClick(x, y);
                }
            }
        });
        return square;
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            JPanel square = createSquare(i);
            boardPanel.add(square);
        }
    }

    public void setDefaultTileBackground(int index, JPanel panel) {
        Color backgroundColor = ((index / BOARD_SIZE) + (index % BOARD_SIZE)) % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY;
        panel.setBackground(backgroundColor);
    }

    private void placeChessPieces() {
        ChessPieceFactoryImpl factImpl = ChessPieceFactoryImpl.INSTANCE;
        IntStream.range(0, BOARD_SIZE).forEach(i -> placePieceOnboard(factImpl.createChessPiece(Type.PAWN, new Position(i, 6), game.core.Color.WHITE)));
        IntStream.range(0, BOARD_SIZE).forEach(i -> placePieceOnboard(factImpl.createChessPiece(Type.PAWN, new Position(i, 1), game.core.Color.BLACK)));

        Type[] pieceTypes = {Type.ROOK, Type.KNIGHT, Type.BISHOP, Type.QUEEN, Type.KING, Type.BISHOP, Type.KNIGHT, Type.ROOK};

        for (int i = 0; i < BOARD_SIZE; i++) {
            placePieceOnboard(factImpl.createChessPiece(pieceTypes[i], new Position(i, 7), game.core.Color.WHITE));
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            placePieceOnboard(factImpl.createChessPiece(pieceTypes[i], new Position(i, 0), game.core.Color.BLACK));
        }

    }




    // 추가 UI 메서드 구현
    public void placePieceOnboard(ChessPiece chessPiece) {
        try {
            chessGameState.addChessPiece(chessPiece); // 내부 리스트에 체스말 추가
            Icon icon = IconLoader.loadIcon(chessPiece.getType(), chessPiece.getColor());
            JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
            JPanel panel = getPanelAtPosition(chessPiece.getPosition());
            addPieceToPanel(panel, pieceLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addPieceToPanel(JPanel panel, JLabel pieceLabel) {
        panel.removeAll();
        panel.setLayout(new GridBagLayout());
        panel.add(pieceLabel);
        panel.validate();
    }
    @Override
    public void onPieceMoved(Position newPosition, ChessPiece piece) {
        Position oldPosition = piece.getPosition();
        int oldIndex = oldPosition.y() * BOARD_SIZE + oldPosition.x();

        JPanel oldPanel = getPanelAtPosition(oldPosition);
        clearSpecificPanel(oldPanel, oldIndex);

        JPanel newPanel = getPanelAtPosition(newPosition);
        addPieceToPanel(newPanel, piece);

        updateUI(oldPanel, newPanel);
    }

    private void updateUI(JPanel oldPanel, JPanel newPanel) {
        oldPanel.revalidate();
        oldPanel.repaint();

        newPanel.revalidate();
        newPanel.repaint();
    }

    private void addPieceToPanel(JPanel panel, ChessPiece piece) {
        Icon icon = IconLoader.loadIcon(piece.getType(), piece.getColor());
        panel.add(new JLabel(icon));
    }

    void clearSpecificPanel(JPanel panel, int index) {
        panel.removeAll();
        setDefaultTileBackground(index, panel);
    }
    @Override
    public void onPieceSelected(ChessPiece piece) {
        highlightPossibleMoves(piece);

    }

    @Override
    public void onInvalidMoveAttempted(String reason) {
        JOptionPane.showMessageDialog(null,reason);
    }

    @Override
    public void onMovesCalculated(List<Position> moves) {

    }


}
