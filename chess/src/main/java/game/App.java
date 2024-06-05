package game;

import game.command.CommandInvoker;
import game.core.ChessGameTurn;
import game.login.LoginFrame;
import game.object.*;
import game.observer.ChessObserver;
import game.status.DrawCondition;
import game.status.VictoryCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8080;
    private static final int LOG_SERVER_PORT = 8000;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            initializeUI();
            connectToServer();
            new LoginFrame();
        });
    }

    private static void connectToServer() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("Hello, server");
                } catch (IOException e) {
                    log.error("Failed to connect to server", e);
                }
                return null;
            }
        }.execute();
    }

    private static void initializeUI() {
        JFrame primaryFrame = createPrimaryFrame();
        ChessGameState chessGameState = new ChessGameState();
        CommandInvoker commandInvoker = new CommandInvoker();
        DrawCondition drawCondition = new DrawCondition();
        GameUtils gameUtils = new GameUtils();
        VictoryCondition victoryCondition = new VictoryCondition();
        ChessGameTurn chessGameTurn = new ChessGameTurn(drawCondition, victoryCondition);

        ChessBoardUI chessBoardUI = createChessBoardUI(chessGameState, primaryFrame);
        ChessGameLogic chessGameLogic = createChessGameLogic(chessGameTurn, commandInvoker, chessBoardUI, chessGameState);

        setupGameLogic(chessGameLogic, chessBoardUI, chessGameState, chessGameTurn, drawCondition, victoryCondition, gameUtils);

        setupPrimaryFrame(primaryFrame, chessBoardUI);
        setupLogFrame(primaryFrame, chessGameTurn);

        sendLogToServer();
    }

    private static ChessBoardUI createChessBoardUI(ChessGameState chessGameState, JFrame primaryFrame) {
        return new ChessBoardUI(chessGameState, primaryFrame);
    }


    private static ChessGameLogic createChessGameLogic(ChessGameTurn chessGameTurn, CommandInvoker commandInvoker, ChessBoardUI chessBoardUI, ChessGameState chessGameState) {
        CastlingLogic castlingLogic = new CastlingLogic(chessBoardUI);
        PromotionLogic promotionLogic = new PromotionLogic(chessGameState, chessBoardUI);
        ChessGameLogic chessGameLogic = new ChessGameLogic(chessGameTurn, commandInvoker, castlingLogic, promotionLogic);
        chessGameLogic.setGameEventListener(chessBoardUI, chessGameState);
        castlingLogic.setCastlingLogic(chessGameState, chessGameLogic);
        return chessGameLogic;
    }

    private static void setupGameLogic(ChessGameLogic chessGameLogic, ChessBoardUI chessBoardUI, ChessGameState chessGameState, ChessGameTurn chessGameTurn, DrawCondition drawCondition, VictoryCondition victoryCondition, GameUtils gameUtils) {
        chessBoardUI.setGameLogicActions(chessGameLogic);
        chessGameTurn.setChessGameState(chessGameState);
        chessGameState.setGameLogicActions(chessGameLogic);

        victoryCondition.setVictoryCondition(chessGameState, gameUtils, chessGameTurn);
        drawCondition.setDrawCondition(chessGameState, chessGameLogic, chessGameTurn);
    }

    private static void setupPrimaryFrame(JFrame primaryFrame, ChessBoardUI chessBoardUI) {
        ChessGameLauncher.createAndShowGUI(primaryFrame);
        primaryFrame.setContentPane(chessBoardUI.getBoardPanel());
        centerFrameOnScreen(primaryFrame);
        primaryFrame.setVisible(true);
    }

    private static void setupLogFrame(JFrame primaryFrame, ChessGameTurn chessGameTurn) {
        JFrame logFrame = createLogFrame(primaryFrame, chessGameTurn);
        logFrame.setVisible(true);
    }

    private static void sendLogToServer() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try (Socket socket = new Socket(SERVER_ADDRESS, LOG_SERVER_PORT)) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    String logMessageWithPrefix = "Client: Game initialized";
                    out.println(logMessageWithPrefix);

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String response = in.readLine();
                    System.out.println("Server response: " + response);
                } catch (IOException e) {
                    log.error("Failed to send log to server", e);
                }
                return null;
            }
        }.execute();
    }

    private static JFrame createLogFrame(JFrame primaryFrame, ChessGameTurn chessGameTurn) {
        JFrame logFrame = new JFrame("Game Log");
        logFrame.setSize(600, 100);
        logFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        logFrame.setLocationRelativeTo(primaryFrame);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        logFrame.add(scrollPane, BorderLayout.CENTER);

        ChessObserver observer = new ChessObserver();
        GameLog gameLog = new GameLog(observer, textArea);
        chessGameTurn.addObserver(gameLog);

        return logFrame;
    }

    private static JFrame createPrimaryFrame() {
        JFrame primaryFrame = new JFrame("Chess Game");
        primaryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        primaryFrame.setSize(600, 650);
        return primaryFrame;
    }

    private static void centerFrameOnScreen(JFrame frame) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
    }
}