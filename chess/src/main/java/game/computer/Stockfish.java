package game.computer;

import java.io.*;

public class Stockfish {
    private Process engineProcess;
    private BufferedReader processReader;
    private PrintWriter processWriter;

    public boolean startEngine(String path) {
        try {
            engineProcess = Runtime.getRuntime().exec(path);
            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new PrintWriter(new OutputStreamWriter(engineProcess.getOutputStream()), true);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
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


    public static void main(String[] args) {
        Stockfish stockfish = new Stockfish();
        if (stockfish.startEngine("/opt/homebrew/bin/stockfish")) {
            stockfish.sendCommand("uci");
            System.out.println(stockfish.getOutput(1000)); // 1초 대기 후 출력 읽기

            stockfish.sendCommand("isready");
            System.out.println(stockfish.getOutput(1000)); // 1초 대기 후 출력 읽기

            stockfish.sendCommand("position startpos moves e2e4 e7e5");
            stockfish.sendCommand("go movetime 1000");
            System.out.println(stockfish.getOutput(2000)); // 2초 대기 후 출력 읽기

            stockfish.stopEngine();
        }
    }
}
