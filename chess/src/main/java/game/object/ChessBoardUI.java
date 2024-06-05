package game.object;

import game.GameUtils;
import game.Position;
import game.factory.*;
import game.ui.IconLoader;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static game.core.Color.*;

public class ChessBoardUI extends JFrame implements GameEventListener {
    @Getter
    private  JPanel boardPanel;
    private final JPanel statusPanel = new JPanel();
    private final ChessGameState chessGameState;
    private static final int BOARD_SIZE = 8;
    @Setter
    private GameLogicActions gameLogicActions;
    @Getter
    private static final Set<Position> highlightedPositions = new HashSet<>();
    private static final Color LIGHT_SQUARE_COLOR = Color.WHITE;
    private static final Color DARK_SQUARE_COLOR = Color.GRAY;



    public ChessBoardUI(ChessGameState chessGameState, JFrame primaryFrame) {
        this.chessGameState = chessGameState;
        initializeBoard();
        placeChessPieces();
        System.out.println(primaryFrame);
    }

    private void initializeStatus() {
        statusPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setPreferredSize(new Dimension(800, 50));
        statusPanel.setBackground(Color.LIGHT_GRAY);
        JLabel label = new JLabel("White's turn");
        statusPanel.add(label);
        statusPanel.setVisible(true);
    }

    public void highlightMoves(List<Position> moves) {
        clearHighlights();
        moves.forEach(this::highlightSingleMove);
    }

    public void highlightSingleMove(Position position) {
        JPanel square = getPanelAtPosition(position);
        if(square != null){
            square.setBackground(Color.YELLOW);
        }
    }


    public void highlightPossibleMoves(ChessPiece piece) {
        clearHighlights();
        getHighlightedPositions().clear();
        List<Position> moves = piece.calculateMoves(chessGameState,new GameUtils()) ;// 이동 가능한 위치 계산
        moves.removeIf(move ->move.x() <0 || move.x()>= BOARD_SIZE || move.y()<0 || move.y()>= BOARD_SIZE); // 보드 바깥으로 나가는 위치 제거
        if(gameLogicActions.isKingInCheck(piece.getColor())){
            moves.removeIf(move -> !canMoveReleaseCheck(piece, move));
        }
        highlightMoves(moves);
        highlightedPositions.addAll(moves);
    }
    private boolean canMoveReleaseCheck(ChessPiece piece, Position move) {
        ChessPiece tempPiece = new ChessPiece(piece.getType(), move, piece.getColor());
        chessGameState.addChessPiece(tempPiece);
        boolean isCheckAfterMove = gameLogicActions.isKingInCheck(piece.getColor());
        chessGameState.removeChessPiece(tempPiece);

        return !isCheckAfterMove;
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
            return null;
        }

        Component comp = boardPanel.getComponent(index);
        if (comp instanceof JPanel) {
            return (JPanel) comp;
        } else {
            return null;
        }
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
        boardPanel = new JPanel(new GridLayout(8, 8));
        boardPanel.setPreferredSize(new Dimension(600,600));
        add(boardPanel, BorderLayout.CENTER);
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
        IntStream.range(0, BOARD_SIZE).forEach(i -> placePieceOnboard(factImpl.createChessPiece(PieceType.PAWN, new Position(i, 6), WHITE)));
        IntStream.range(0, BOARD_SIZE).forEach(i -> placePieceOnboard(factImpl.createChessPiece(PieceType.PAWN, new Position(i, 1), BLACK)));

        PieceType[] pieceTypes = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN, PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};

        for (int i = 0; i < BOARD_SIZE; i++) {
            placePieceOnboard(factImpl.createChessPiece(pieceTypes[i], new Position(i, 7), WHITE));
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            placePieceOnboard(factImpl.createChessPiece(pieceTypes[i], new Position(i, 0), BLACK));
        }

    }
    @Override
    public void onGameDraw(){
        JOptionPane.showMessageDialog(null, "Draw");
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
        oldPanel.repaint();

        newPanel.revalidate();
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
        JOptionPane.showMessageDialog(null,reason);
    }




}
