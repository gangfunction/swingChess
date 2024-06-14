package game.command;

import game.Position;
import game.core.factory.ChessPiece;
import game.model.state.CapturedPieceManager;
import game.model.state.ChessPieceManager;
import game.model.state.MoveManager;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandPool {
    private final Deque<MoveCommand> pool = new ArrayDeque<>();

    public MoveCommand getCommand(ChessPiece piece,
                                  Position start, Position end,
                                  CapturedPieceManager capturedPieceManager,
                                  ChessPieceManager chessPieceManager,
                                  MoveManager moveManager
    ) {
        if (pool.isEmpty()) {
            return new MoveCommand(piece, start, end,
                    capturedPieceManager,
                    chessPieceManager,
                    moveManager);
        } else {
            MoveCommand command = pool.pop();
            command.reset(piece, start, end,
                    capturedPieceManager,
                    chessPieceManager,
                    moveManager);
            return command;
        }
    }

    public void returnCommand(MoveCommand command) {
        pool.push(command);
    }
}
