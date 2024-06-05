package game.command;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandInvoker {
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    /**
     * Executes a command and stores it in the undo stack.
     * Clears the redo stack as new command execution invalidates the redo history.
     *
     * @param command the command to execute
     */
    public synchronized void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }


    public synchronized void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    public synchronized void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.redo();
            undoStack.push(command);
        }
    }
}