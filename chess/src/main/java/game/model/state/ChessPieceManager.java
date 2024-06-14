package game.model.state;

import game.Position;
import game.core.factory.ChessPiece;
import game.util.Color;

import java.util.Map;

public interface ChessPieceManager {

    void addChessPiece(Position move, ChessPiece chessPiece);

    ChessPiece getChessPieceAt(Position targetPosition);

    ChessPiece getSelectedPiece();

    void setSelectedPiece(ChessPiece piece);

    void removeChessPiece(ChessPiece targetPawn);

    ChessPiece getKing(Color color);

    Map<Position, ChessPiece> getChessPieces();
}