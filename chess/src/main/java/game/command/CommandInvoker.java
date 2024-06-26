package game.command;

import game.Position;
import game.core.ChessGameTurn;
import game.core.factory.ChessPiece;
import game.model.state.CapturedPieceManager;
import game.model.state.ChessPieceManager;
import game.model.state.SpecialMoveManager;
import game.model.state.MoveManager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CommandInvoker {
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();
    private final List<Runnable> listeners = new ArrayList<>();
    private final CommandPool commandPool = new CommandPool();

    public synchronized MoveCommand executeCommand(ChessPiece piece,
                                                   Position start,
                                                   Position end,
                                                   SpecialMoveManager state,
                                                   ChessGameTurn turn,
                                                   CapturedPieceManager capturedPieceManager,
                                                   ChessPieceManager chessPieceManager,
                                                   MoveManager moveManager
    ) {
        MoveCommand command = commandPool.getCommand(piece, start, end,
                capturedPieceManager,
                chessPieceManager,
                moveManager);
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