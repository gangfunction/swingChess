package game.model.state;

import game.core.factory.ChessPiece;

import java.util.Stack;

public interface CapturedPieceManager {
    Stack<ChessPiece> getCapturedPieces();
}