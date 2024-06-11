package game.model;

import game.Position;
import game.model.state.ChessPieceManager;
import game.model.state.SpecialMoveManager;
import game.ui.GameEventListener;
import game.util.Color;
import game.core.factory.ChessPiece;
import game.util.PieceType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;


public class PromotionLogic implements ActionListener {

    JButton queenButton;
    JButton rookButton;
    JButton bishopButton;
    JButton knightButton;
    PieceType promotionPieceType = null;
    private static final Logger logger = Logger.getLogger(PromotionLogic.class.getName());
    private final SpecialMoveManager specialMoveManager;
    private final GameEventListener gameEventListener;

    private final Set<ChessPiece> promotedPawns = new HashSet<>();
    private final ChessPieceManager chessPieceManager;

    public PromotionLogic(SpecialMoveManager specialMoveManager,
                          GameEventListener gameEventListener,
                          ChessPieceManager chessPieceManager
    ) {
        this.specialMoveManager = specialMoveManager;
        this.chessPieceManager = chessPieceManager;
        this.gameEventListener = gameEventListener;
    }

    public void promotePawn(ChessPiece pawn, Position promotionPosition) {
        if (specialMoveManager == null || gameEventListener == null) {
            logger.info("Listeners must not be null");
        }
        if (canPromote(pawn, promotionPosition) && !promotedPawns.contains(pawn)) {
            showAndSelectType(pawn, promotionPosition);
            promotedPawns.add(pawn);

        }
    }

    private void showAndSelectType(ChessPiece pawn, Position promotionPosition) {
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = createPromotionDialog();
            dialog.setVisible(true);

            if (promotionPieceType != null) {
                handlePromotion(pawn, promotionPosition);
            } else {
                logger.warning("Promotion type was not selected.");
            }
        });
    }

    private JDialog createPromotionDialog() {
        JDialog dialog = new JDialog();
        setupDialog(dialog);
        dialog.add(createPromotionButton("Queen", PieceType.QUEEN, dialog));
        dialog.add(createPromotionButton("Rook", PieceType.ROOK, dialog));
        dialog.add(createPromotionButton("Bishop", PieceType.BISHOP, dialog));
        dialog.add(createPromotionButton("Knight", PieceType.KNIGHT, dialog));
        return dialog;
    }

    private void handlePromotion(ChessPiece pawn, Position promotionPosition) {
        Color color = pawn.getColor();
        gameEventListener.onPieceMoved(promotionPosition, pawn);
        ChessPiece chessPiece = chessPieceManager.getChessPieces().get(promotionPosition);
        chessPieceManager.removeChessPiece(chessPiece);
        ChessPiece newPiece = createNewPiece(color, promotionPosition, promotionPieceType);
        gameEventListener.onPieceMoved(promotionPosition, newPiece);
        chessPieceManager.addChessPiece(promotionPosition, newPiece);
    }

    private static void setupDialog(JDialog dialog) {
        dialog.setTitle("Promotion");
        dialog.setModal(true);
        dialog.setSize(300, 100);
        dialog.setLayout(new FlowLayout());
        dialog.setLocationRelativeTo(null);
    }

    private JButton createPromotionButton(String label, PieceType pieceType, JDialog dialog) {
        JButton button = new JButton(label);
        button.addActionListener(e -> {
            promotionPieceType = pieceType;
            dialog.dispose();
        });
        return button;
    }

    boolean isAtPromotionRank(Position position) {
        return position.y() == 0 || position.y() == 7;
    }

    private boolean canPromote(ChessPiece pawn, Position position) {
        return pawn.getType() == PieceType.PAWN && isAtPromotionRank(position);
    }

    private ChessPiece createNewPiece(Color color, Position position, PieceType newPieceType) {
        return new ChessPiece(newPieceType, position, color);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == rookButton) {
            promotionPieceType = PieceType.ROOK;
        } else if (e.getSource() == bishopButton) {
            promotionPieceType = PieceType.BISHOP;
        } else if (e.getSource() == knightButton) {
            promotionPieceType = PieceType.KNIGHT;
        } else if (e.getSource() == queenButton) {
            promotionPieceType = PieceType.QUEEN;
        }
    }
}