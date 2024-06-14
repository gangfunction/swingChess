package game.computer;

import java.io.*;

public class Stockfish {
    private Process engineProcess;
    private BufferedReader processReader;
    private PrintWriter processWriter;

    private static final String PATH = "/opt/homebrew/bin/stockfish";
    public void startEngine() {
        try {
            engineProcess = Runtime.getRuntime().exec(PATH);
            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new PrintWriter(new OutputStreamWriter(engineProcess.getOutputStream()), true);

            checkEngine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkEngine() {
        sendCommand("uci");
        String output = getOutput(1000);
        if (output.contains("uciok")) {
            System.out.println("Stockfish engine has started successfully");
        }
    }

    public void sendCommand(String command) {
        processWriter.println(command);
    }

    public String getOutput(int waitTime) {
        StringBuilder output = new StringBuilder();
        try {
            Thread.sleep(waitTime);
            while (processReader.ready()) {
                output.append(processReader.readLine()).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public void stopEngine() {
        sendCommand("quit");
        try {
            processReader.close();
            processWriter.close();
            engineProcess.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
