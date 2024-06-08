package game.command;

import game.Position;
import game.core.ChessGameTurn;
import game.factory.ChessPiece;
import game.object.GameStatusListener;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandPool {
    private final Deque<MoveCommand> pool = new ArrayDeque<>();

    public MoveCommand getCommand(ChessPiece piece, Position start, Position end, GameStatusListener state, ChessGameTurn turn) {
        if (pool.isEmpty()) {
            return new MoveCommand(piece, start, end, state, turn);
        } else {
            MoveCommand command = pool.pop();
            command.reset(piece, start, end, state, turn);
            return command;
        }
    }

    public void returnCommand(MoveCommand command) {
        pool.push(command);
    }
}
