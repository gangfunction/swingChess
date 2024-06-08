package game.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerCommunicator {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8080;
    private static final int LOG_SERVER_PORT = 8000;

    static void connectToServer() {
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

    static void sendLogToServer() {
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

    public static void disconnectFromServer() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("Goodbye, server");
                } catch (IOException e) {
                    log.error("Failed to disconnect from server", e);
                }
                return null;
            }
        }.execute();
    }
}
