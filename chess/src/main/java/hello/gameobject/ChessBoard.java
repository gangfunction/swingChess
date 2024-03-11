package hello.gameobject;

import hello.*;
import hello.command.CommandInvoker;
import hello.core.ChessGame;
import hello.core.Player;
import hello.factory.*;
import hello.observer.ChessObserver;
import hello.ui.IconLoader;
import hello.ui.UIManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChessBoard {
    public ChessBoard(GameLog gameLog) {
        this.chessGame = new ChessGame();
        this.gameLog = gameLog;
        this.gameLogic = new GameLogic(this, chessGame, new CommandInvoker());
        initializeBoard();
        placeChessPieces();
        capturedPiecesPanel = new JPanel();
        capturedPiecesPanel.setLayout(new FlowLayout());
    }


    private final GameLog gameLog;

    private ChessObserver chessObserver;

    private final ChessGame chessGame;
    // 게임 시작 색상

    private final JPanel capturedPiecesPanel;

    private final GameUtils DistanceManager = new GameUtils();
    private final UIManager UIManager = new UIManager(this);
    private final PieceSelectionManager pieceSelectionManager = new PieceSelectionManager(this);
    private final GameLogic gameLogic;
    private final List<Position> highlightedPositions = new ArrayList<>();
    private final JPanel boardPanel = new JPanel(new GridLayout(8, 8));
    private final List<ChessPiece> chessPieces = new ArrayList<>();

    private ChessPiece selectedPiece = null;


    private void initializeBoard() {
        for (int i = 0; i < 64; i++) {
            JPanel square = createSquare(i);
            boardPanel.add(square);
        }
    }

    private JPanel createSquare(int index) {
        final int x = index % 8;
        final int y = index / 8;
        JPanel square = new JPanel(new GridBagLayout());
        square.setBackground((x + y) % 2 == 0 ? Color.WHITE : Color.GRAY);
        square.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("x: " + x + ", y: " + y);
                gameLogic.handleSquareClick(x, y);
            }
        });
        return square;
    }

    public void addPieceToBoard(ChessPiece chessPiece) {
        chessPieces.add(chessPiece); // 내부 리스트에 체스말 추가
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


    private void placeChessPieces() {
// 기본 배치를 위한 행 - 폰들과 다른 말들
        for (int i = 0; i < 8; i++) {
            addPieceToBoard(new PawnFactory().createChessPiece(new Position(i, 6), Player.Color.WHITE));
            addPieceToBoard(new PawnFactory().createChessPiece(new Position(i, 1), Player.Color.BLACK));
        }

        // 특수한 말 배치
        placePieceRow(0, Player.Color.BLACK);
        placePieceRow(7, Player.Color.WHITE);
    }




    private void placePieceRow(int y, Player.Color color) {
        // 각 (룩, 나이트, 비숍, 퀸, 킹, 비숍, 나이트, 룩) 위치에 배치
        addPieceToBoard(RookFactory.getInstance().createChessPiece(new Position(0, y), color));
        addPieceToBoard(KnightFactory.getInstance().createChessPiece(new Position(1, y), color));
        addPieceToBoard(BishopFactory.getInstance().createChessPiece(new Position(2, y), color));
        addPieceToBoard(QueenFactory.getInstance().createChessPiece(new Position(3, y), color));
        addPieceToBoard(KingFactory.getInstance().createChessPiece(new Position(4, y), color));
        addPieceToBoard(BishopFactory.getInstance().createChessPiece(new Position(5, y), color));
        addPieceToBoard(KnightFactory.getInstance().createChessPiece(new Position(6, y), color));
        addPieceToBoard(RookFactory.getInstance().createChessPiece(new Position(7, y), color));
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

    private void logMove(ChessPiece piece, Position newPosition) {
        String logMessage = piece.getPosition() + " moved to " + newPosition;
        gameLog.logAction(logMessage);
    }

    public void movePiece(ChessPiece piece, Position newPosition) {
        String logMessage = piece.getPosition() + " moved to " + newPosition;
        gameLog.logAction(logMessage);

        Position oldPosition = piece.getPosition();
        // UI 업데이트: 새 위치에 기물을 배치하고, 이전 위치를 빈 공간으로 만듦
        UIManager.updateUIAfterMove(oldPosition, newPosition, piece);
    }





    public void setDefaultTileBackground(int index, JPanel panel) {
        Color backgroundColor = ((index / 8) + (index % 8)) % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY;
        panel.setBackground(backgroundColor);
    }


    public ChessPiece getSelectedPiece() {
        return selectedPiece;
    }

    public GameUtils getDistanceManager() {
        return DistanceManager;
    }

    public PieceSelectionManager getPieceSelectionManager() {
        return pieceSelectionManager;
    }

    public hello.ui.UIManager getUIManager() {
        return UIManager;
    }

    public List<Position> getHighlightedPositions() {
        return highlightedPositions;
    }

    public void setSelectedPiece(ChessPiece piece) {
        selectedPiece = piece;
    }

    public Player.Color getCurrentPlayerColor() {
        return chessGame.getCurrentPlayerColor();
    }

    public void highlightPossibleMoves(ChessPiece selectedPiece) {
        gameLogic.highlightPossibleMoves(selectedPiece);
    }


    public void clearSquare(Position position) {
        int index = position.getY() * 8 + position.getX();
        JPanel panel = (JPanel) getBoardPanel().getComponent(index);
        panel.removeAll();
        panel.revalidate();
        panel.repaint();
    }

    public List<ChessPiece> getBoard() {
        return chessPieces;
    }
}
