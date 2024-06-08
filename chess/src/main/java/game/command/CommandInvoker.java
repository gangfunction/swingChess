package game.command;

import game.Position;
import game.core.ChessGameTurn;
import game.factory.ChessPiece;
import game.object.GameStatusListener;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CommandInvoker {
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();
    private final List<Runnable> listeners = new ArrayList<>();
    private final CommandPool commandPool = new CommandPool();

    /**
     * Executes a command and stores it in the undo stack.
     * Clears the redo stack as new command execution invalidates the redo history.
     *
     */
    public synchronized MoveCommand executeCommand(ChessPiece piece, Position start, Position end, GameStatusListener state, ChessGameTurn turn) {
        MoveCommand command = commandPool.getCommand(piece, start, end, state, turn);
        command.execute();
        undoStack.push(command);
        int MAX_STACK_SIZE = 100;
        if (undoStack.size() >= MAX_STACK_SIZE) {
            undoStack.removeLast();
        }
        redoStack.clear();
        new Thread(this::notifyListeners).start();
        return command;
    }


    public synchronized void undo() {
        if (!undoStack.isEmpty()) {
            MoveCommand command = (MoveCommand) undoStack.pop();
            command.undo();
            redoStack.push(command);
            notifyListeners();
        }
    }

    public void addUndoRedoListener(Runnable listener) {
        listeners.add(listener);
    }

    public synchronized void redo() {
        if (!redoStack.isEmpty()) {
            MoveCommand command = (MoveCommand) redoStack.pop();
            command.redo();
            undoStack.push(command);
            notifyListeners();
        }
    }
    private void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    public void returnCommand(MoveCommand moveCommand) {
        commandPool.returnCommand(moveCommand);
    }
}