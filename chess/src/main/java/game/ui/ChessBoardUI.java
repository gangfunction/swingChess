package game.ui;

import game.Position;
import game.core.factory.ChessPiece;
import game.core.factory.ChessPieceFactoryImpl;
import game.model.state.ChessGameState;
import game.model.GameLogicActions;
import game.model.state.ChessPieceManager;
import game.util.PieceType;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.stream.IntStream;

import static game.util.Color.BLACK;
import static game.util.Color.WHITE;


public class ChessBoardUI extends JFrame implements GameEventListener {
    private static final Logger log = LoggerFactory.getLogger(ChessBoardUI.class);
    @Getter
    private JPanel boardPanel;
    private final transient ChessGameState chessGameState;
    private static final int BOARD_SIZE = 8;
    @Setter
    private transient GameLogicActions gameLogicActions;
    @Getter
    private static final Set<Position> highlightedPositions = new HashSet<>();
    private static final Color LIGHT_SQUARE_COLOR = Color.WHITE;
    private static final Color DARK_SQUARE_COLOR = Color.GRAY;
    private final ChessPieceManager chessPieceManager;


    public ChessBoardUI(ChessGameState chessGameState, ChessPieceManager chessPieceManager) {
        this.chessGameState = chessGameState;
        this.chessPieceManager = chessPieceManager;
        initializeBoard();
        initializeStatusBar();
        initializeCapturedPiecesPanel();
        boardPanel.setDoubleBuffered(true);
    }


    private void initializeStatusBar() {
        JLabel statusBar = new JLabel("White's turn");
        add(statusBar, BorderLayout.SOUTH);
    }

    private void initializeCapturedPiecesPanel() {
        JPanel capturedPiecesPanel = new JPanel();
        capturedPiecesPanel.setLayout(new GridLayout(2, 8));
        add(capturedPiecesPanel, BorderLayout.NORTH);
    }


    @Override
    public Dimension getPreferredSize() {
        int size = Math.min(getWidth(), getHeight());
        return new Dimension(size, size);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        int size = Math.min(getWidth(), getHeight());
        for (Component comp : getComponents()) {
            comp.setBounds(0, 0, size, size);
        }
    }

    public void highlightMoves(Set<Position> moves) {
        clearHighlights();
        for (Position move : moves) {
            highlightSingleMove(move);
        }
    }

    public void highlightSingleMove(Position position) {
        JPanel square = getPanelAtPosition(position);
        if (square != null) {
            square.setBackground(Color.YELLOW);
        }
    }

    public void highlightPossibleMoves(ChessPiece piece) {
        clearHighlights();
        getHighlightedPositions().clear();
        Set<Position> moves = piece.calculateMoves(chessGameState, chessGameState);// 이동 가능한 위치 계산
        moves.removeIf(this::isOutOfBounds);
        highlightMoves(moves);
        highlightedPositions.addAll(moves);
    }

    private boolean isOutOfBounds(Position move) {
        return move.x() < 0 || move.x() >= BOARD_SIZE || move.y() < 0 || move.y() >= BOARD_SIZE;
    }


    public void clearHighlights() {
        if (getBoardPanel() == null) {
            return;
        }
        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            JPanel square = (JPanel) getBoardPanel().getComponent(i);
            if (square != null) {
                setDefaultTileBackground(i, square);
            }
        }
    }

    private final Map<Position, JPanel> panelCache = new HashMap<>();

    public JPanel getPanelAtPosition(Position position) {
        return panelCache.computeIfAbsent(position, pos -> {
            int index = pos.y() * BOARD_SIZE + pos.x();
            if (index < 0 || index >= boardPanel.getComponentCount()) {
                return null;
            }
            Component comp = boardPanel.getComponent(index);
            return (comp instanceof JPanel) ? (JPanel) comp : null;
        });
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
                if (gameLogicActions != null && SwingUtilities.isLeftMouseButton(e)) {
                    gameLogicActions.handleSquareClick(x, y);
                }
            }
        });
        return square;
    }

    private void initializeBoard() {
        boardPanel = new JPanel(new GridLayout(8, 8));
        boardPanel.setPreferredSize(new Dimension(600, 600));
        add(boardPanel, BorderLayout.CENTER);
        createSquares();
        placeChessPieces();


    }

    private void createSquares() {
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
        IntStream.range(0, BOARD_SIZE).forEach(i -> placePieceOnboard(new Position(i, 6), factImpl.createChessPiece(PieceType.PAWN, new Position(i, 6), WHITE)));
        IntStream.range(0, BOARD_SIZE).forEach(i -> placePieceOnboard(new Position(i, 1), factImpl.createChessPiece(PieceType.PAWN, new Position(i, 1), BLACK)));

        PieceType[] pieceTypes = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN, PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};

        for (int i = 0; i < BOARD_SIZE; i++) {
            placePieceOnboard(new Position(i, 7), factImpl.createChessPiece(pieceTypes[i], new Position(i, 7), WHITE));
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            placePieceOnboard(new Position(i, 0), factImpl.createChessPiece(pieceTypes[i], new Position(i, 0), BLACK));
        }

    }

    @Override
    public void placePieceOnboard(Position move, ChessPiece chessPiece) {
        try {
            chessPieceManager.addChessPiece(move, chessPiece);
            Icon icon = IconLoader.loadIcon(chessPiece.getType(), chessPiece.getColor());
            JLabel pieceLabel = new JLabel(icon, SwingConstants.CENTER);
            JPanel panel = getPanelAtPosition(chessPiece.getPosition());
            addPieceToPanel(panel, pieceLabel);
        } catch (Exception e) {
            log.error("Failed to place piece on board", e);
        }
    }

    @Override
    public void onPieceRemoved(ChessPiece piece) {
        SwingUtilities.invokeLater(() -> {
            JPanel panel = getPanelAtPosition(piece.getPosition());
            clearSpecificPanel(panel, piece.getPosition().y() * BOARD_SIZE + piece.getPosition().x());
        });
    }

    @Override
    public void addPieceToPanel(JPanel panel, JLabel pieceLabel) {
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
        putPieceToPanel(newPanel, piece);
        updateUI(oldPanel, newPanel);
    }

    private void updateUI(JPanel oldPanel, JPanel newPanel) {
        oldPanel.revalidate();
        newPanel.revalidate();
        oldPanel.repaint();
        newPanel.repaint();
    }

    private void putPieceToPanel(JPanel panel, ChessPiece piece) {
        Icon icon = IconLoader.loadIcon(piece.getType(), piece.getColor());
        panel.add(new JLabel(icon));
    }

    void clearSpecificPanel(JPanel panel, int index) {
        panel.removeAll();
        setDefaultTileBackground(index, panel);
    }

    @Override
    public void onInvalidMoveAttempted(String reason) {
        JOptionPane.showMessageDialog(null, reason);
    }

}
