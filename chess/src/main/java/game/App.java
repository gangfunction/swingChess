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
    private static Logger log = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            initializeUI();
            connectToServer();
            new LoginFrame();
        });
    }

    private static void connectToServer() {
        try(Socket socket = new Socket("127.0.0.1",8080)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Hello, server");

        } catch (IOException e) {
            log.error("Failed to connect to server", e);
        }
    }

    private static void initializeUI() {
        JFrame primaryFrame = createPrimaryFrame();

        ChessGameState chessGameState = new ChessGameState();
        CommandInvoker commandInvoker = new CommandInvoker();
        DrawCondition drawCondition = new DrawCondition();
        GameUtils gameUtils = new GameUtils();
        VictoryCondition victoryCondition = new VictoryCondition();
        ChessGameTurn chessGameTurn = new ChessGameTurn(drawCondition,victoryCondition);

        ChessBoardUI chessBoardUI = new ChessBoardUI(chessGameState);
        CastlingLogic castlingLogic = new CastlingLogic(chessBoardUI);
        PromotionLogic promotionLogic = new PromotionLogic(chessGameState, chessBoardUI,chessGameTurn);

        ChessGameLogic chessGameLogic = new ChessGameLogic(chessGameTurn, commandInvoker, castlingLogic,promotionLogic);
        chessGameLogic.setGameEventListener(chessBoardUI,chessGameState);
        castlingLogic.setCastlingLogic(chessGameState, chessGameLogic);


        chessBoardUI.setGameLogicActions(chessGameLogic);
        chessGameTurn.setChessGameState(chessGameState);
        chessGameState.setGameLogicActions(chessGameLogic, chessBoardUI);

        victoryCondition.setVictoryCondition(chessGameState, gameUtils, chessGameTurn);
        drawCondition.setDrawCondition(chessGameState, chessGameLogic, chessGameTurn);

        ChessGameLauncher.createAndShowGUI(primaryFrame);

        primaryFrame.setContentPane(chessBoardUI.getBoardPanel());

        centerFrameOnScreen(primaryFrame);
        primaryFrame.setVisible(true);
        JFrame logFrame = createLogFrame(primaryFrame, chessGameTurn);

        logFrame.setVisible(true);
        sendLogToServer("Game initialized");
    }

    private static void sendLogToServer(String logMessage){
        try{
            Socket socket = new Socket("127.0.0.1", 8000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String logMessageWithPrefix = "Client: " + logMessage;
            out.println(logMessageWithPrefix);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();
            System.out.println("Server response: " + response);

        }catch (IOException e){
            log.error("Failed to send log to server", e);
        }
    }

    private static JFrame createLogFrame(JFrame primaryFrame, ChessGameTurn chessGameTurn) {
        JFrame logFrame = new JFrame("Game Log");
        logFrame.setSize(600, 100);
        logFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int x = primaryFrame.getLocation().x;
        int y = primaryFrame.getLocation().y + primaryFrame.getHeight();
        logFrame.setLocation(x, y);

        JTextArea textArea = new JTextArea();
        logFrame.setVisible(true);
        ChessObserver observer = new ChessObserver();
        GameLog gameLog = new GameLog(observer, textArea);
        chessGameTurn.addObserver(gameLog);

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        logFrame.add(scrollPane, BorderLayout.CENTER);
        return logFrame;
    }

    private static JFrame createPrimaryFrame() {
        JFrame primaryFrame = new JFrame("Chess Game");
        primaryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        primaryFrame.setSize(600, 600);
        return primaryFrame;
    }



    private static void centerFrameOnScreen(JFrame frame) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
    }
}