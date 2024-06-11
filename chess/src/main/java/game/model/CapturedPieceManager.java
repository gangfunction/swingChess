package game.model;

import game.core.factory.ChessPiece;

import java.util.Stack;

public interface CapturedPieceManager {
    Stack<ChessPiece> getCapturedPieces();
}