package game.app;

import game.core.ChessGameTurn;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
public class GameStateManager {
    @Setter
    private static ChessGameTurn chessGameTurn;
    void loadLogic(){
        SwingUtilities.invokeLater(this::loadLogicTarget);
    }
    private void loadLogicTarget() {
        String directoryPath = "chess/game/saves";
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            JOptionPane.showMessageDialog(null, "No saved game state found!");
            return;
        }
        JFileChooser fileChooser = new JFileChooser(directoryPath);
        fileChooser.setDialogTitle("Select a game state file to load");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            loadProcess(file);
        } else {
            log.info("File selection cancelled by user.");
        }
    }

    private static void loadProcess(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            StringBuilder gameStateBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                gameStateBuilder.append(line).append("\n");
            }
            String gameState = gameStateBuilder.toString();
            chessGameTurn.deserializeGameState(gameState);
        } catch (IOException e) {
            log.error("Failed to load game state", e);
            JOptionPane.showMessageDialog(null, "Failed to load game state: " + e.getMessage());
        }
    }

    void saveLogic() {
        String gameState = chessGameTurn.serializeGameState();
        String directoryPath = "chess/game/saves";
        Path path = Paths.get(directoryPath);

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                log.info("Directory created: {}", directoryPath);
            } catch (IOException e) {
                log.error("Failed to create directory", e);
                return;
            }
        }

        String filename = directoryPath + "/chess_game_state.txt";
        File file = new File(filename);

        if (file.exists()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
                    .withZone(ZoneOffset.UTC);
            String utcTime = formatter.format(Instant.now());
            filename = directoryPath + "/chess_game_state_" + utcTime + ".txt";
        }

        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write(gameState);
            log.info("Game state saved successfully to {}", filename);
        } catch (IOException e) {
            log.error("Failed to save game state", e);
        }
    }

}
