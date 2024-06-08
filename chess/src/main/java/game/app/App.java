package game.app;

import game.GameLog;
import org.jdesktop.swingx.JXCollapsiblePane;
import game.core.ChessGameTurn;
import game.login.LoginFrame;
import game.object.*;
import game.observer.ChessObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class App {
    private static final GameStateManager gameStateManager;
    static {
        gameStateManager = new GameStateManager();
    }

    private  JXCollapsiblePane collapsiblePane;
    private  JFrame primaryFrame;
    private  JSplitPane mainSplitPane;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.initializeUI();
            new LoginFrame();
        });
    }

    private  void initializeUI() {
        try {
            primaryFrame = createPrimaryFrame();
            ChessGameManager.initializeGameComponents();
            setupPrimaryFrame(primaryFrame, ChessGameManager.chessBoardUI, ChessGameManager.chessGameTurn);
            ServerCommunicator.connectToServer();
            ServerCommunicator.sendLogToServer();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred during initialization: " + e.getMessage());
        }
    }


    private  void setupPrimaryFrame(JFrame primaryFrame, ChessBoardUI chessBoardUI, ChessGameTurn chessGameTurn) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(chessBoardUI.getBoardPanel());
        splitPane.setBottomComponent(createLogPanel(chessGameTurn));
        splitPane.setResizeWeight(0.8);

        JXCollapsiblePane collapsiblePane = createCollapsibleButtonPanel();
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(collapsiblePane, BorderLayout.CENTER);
        JSplitPane mainSplitPane = getjSplitPane(splitPane, rightPanel);

        primaryFrame.setContentPane(mainSplitPane);
        primaryFrame.pack();
        primaryFrame.setResizable(false);
        centerFrameOnScreen(primaryFrame);
        primaryFrame.setVisible(true);
        primaryFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ServerCommunicator.disconnectFromServer();
                System.exit(0);
            }
        });
    }

    private  JSplitPane getjSplitPane(JSplitPane splitPane, JPanel rightPanel) {
        if (mainSplitPane == null) {
            mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            mainSplitPane.setLeftComponent(splitPane);
            mainSplitPane.setRightComponent(rightPanel);
            mainSplitPane.setResizeWeight(0.8);
            mainSplitPane.setOneTouchExpandable(true);
        }
        return mainSplitPane;
    }

    private  JXCollapsiblePane createCollapsibleButtonPanel() {
        if (collapsiblePane == null) {
            collapsiblePane = new JXCollapsiblePane();
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

            String saveButtonText = "save";
            JButton saveButton = new JButton(saveButtonText);
            saveButton.addActionListener(e -> {
                gameStateManager.saveLogic();
                JOptionPane.showMessageDialog(null, "Game saved!");
            });
            buttonPanel.add(saveButton);

            JButton loadButton = new JButton("Load");
            loadButton.addActionListener(e -> gameStateManager.loadLogic());
            buttonPanel.add(loadButton);

            JButton undoButton = new JButton("Undo");
            undoButton.addActionListener(e -> {
                ChessGameManager.commandInvoker.undo();
                ChessGameManager.updateUI();
                JOptionPane.showMessageDialog(null, "Undo!");
            });
            buttonPanel.add(undoButton);

            JButton redoButton = new JButton("Redo");
            redoButton.addActionListener(e -> {
                ChessGameManager.commandInvoker.redo();
                ChessGameManager.updateUI();
                JOptionPane.showMessageDialog(null, "Redo!");
            });
            buttonPanel.add(redoButton);

            JButton resignButton = new JButton("Resign");
            resignButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(null, "Resign!");
                ChessGameManager.chessGameTurn.endGame();
            });
            buttonPanel.add(resignButton);

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(null, "Exit!");
                System.exit(0);
            });
            buttonPanel.add(exitButton);

            buttonPanel.setVisible(true);

            collapsiblePane.add(buttonPanel);
        }
        return collapsiblePane;
    }

    private  JPanel createLogPanel(ChessGameTurn chessGameTurn) {
        JPanel logPanel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        ChessObserver observer = new ChessObserver();
        GameLog gameLog = new GameLog(observer, textArea);
        chessGameTurn.addObserver(gameLog);

        return logPanel;
    }

    private  JFrame createPrimaryFrame() {
        if (primaryFrame == null) {
            primaryFrame = new JFrame("Chess Game");
            primaryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            primaryFrame.setSize(800, 600);
        }
        return primaryFrame;
    }

    private  void centerFrameOnScreen(JFrame frame) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
    }
}