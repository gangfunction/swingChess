package game.strategy;


import game.Position;
import game.core.factory.ChessPiece;
import game.model.state.ChessPieceManager;
import game.model.state.MoveManager;
import game.strategy.calculator.DiagonalMoveCalculator;

import java.util.Set;

public class BishopStrategy implements MoveStrategy {
    private final DiagonalMoveCalculator diagonalMoveCalculator = new DiagonalMoveCalculator();
    @Override
    public Set<Position> calculateMoves(ChessPieceManager chessPieceManager, MoveManager moveManager, ChessPiece piece ) {
        return diagonalMoveCalculator.calculateMoves(chessPieceManager,moveManager, piece);
    }
}
