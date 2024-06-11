package game.command;

import game.Position;
import game.core.ChessGameTurn;
import game.core.factory.ChessPiece;
import game.model.CapturedPieceManager;
import game.model.GameStatusListener;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandPool {
    private final Deque<MoveCommand> pool = new ArrayDeque<>();

    public MoveCommand getCommand(ChessPiece piece, Position start, Position end, GameStatusListener state, ChessGameTurn turn, CapturedPieceManager capturedPieceManager) {
        if (pool.isEmpty()) {
            return new MoveCommand(piece, start, end, state, turn, capturedPieceManager);
        } else {
            MoveCommand command = pool.pop();
            command.reset(piece, start, end, state, turn, capturedPieceManager);
            return command;
        }
    }

    public void returnCommand(MoveCommand command) {
        pool.push(command);
    }
}
