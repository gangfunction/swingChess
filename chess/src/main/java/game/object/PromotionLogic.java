package game.object;

import game.Position;
import game.core.Color;
import game.core.GameTurnListener;
import game.core.Player;
import game.factory.ChessPiece;
import game.factory.Type;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


public class PromotionLogic implements ActionListener {

    private static final Logger logger = Logger.getLogger(PromotionLogic.class.getName());
    private final GameStatusListener gameStatusListener;
    private final GameEventListener gameEventListener;
    private final GameTurnListener gameTurnListener;

    private final Set<ChessPiece> promotedPawns = new HashSet<>();
    public PromotionLogic(GameStatusListener gameStatusListener, GameEventListener gameEventListener, GameTurnListener gameTurnListener) {
        this.gameStatusListener = gameStatusListener;
        this.gameEventListener = gameEventListener;
        this.gameTurnListener = gameTurnListener;
    }


    public void promotePawn(ChessPiece pawn, Position promotionPosition) {
        if(gameStatusListener == null || gameEventListener == null) {
            logger.info("Listeners must not be null");
        }
        if (canPromote(pawn, promotionPosition) && !promotedPawns.contains(pawn)){
            //promotion의 선택지를 주기
            showAndSelectType(pawn, promotionPosition);
            promotedPawns.add(pawn);

        }


    }
    JButton queenButton, rookButton, bishopButton, knightButton;
    Type promotionType = null;
    private void showAndSelectType(ChessPiece pawn, Position promotionPosition) {
        // Runnable 객체를 생성하여 SwingUtilities.invokeLater 메서드를 사용해 EDT에서 실행합니다.
        SwingUtilities.invokeLater(() -> {
            // 모달 JDialog 생성
            final JDialog dialog = new JDialog();
            dialog.setTitle("Promotion");
            dialog.setModal(true); // 대화상자를 모달로 설정하여, 사용자가 선택을 완료할 때까지 기다립니다.
            dialog.setSize(300, 100);
            dialog.setLayout(new FlowLayout());


            // 버튼을 대화상자에 추가
            dialog.add(createPromotionButton("Queen", Type.QUEEN, dialog));
            dialog.add(createPromotionButton("Rook", Type.ROOK, dialog));
            dialog.add(createPromotionButton("Bishop", Type.BISHOP, dialog));
            dialog.add(createPromotionButton("Knight", Type.KNIGHT, dialog));

            dialog.setLocationRelativeTo(null); // 대화상자를 화면 중앙에 위치시킵니다.
            dialog.setVisible(true); // 대화상자를 보여줍니다.

            logger.info("Promotion type: " + promotionType);
            if (gameStatusListener != null) {
                Color color = pawn.getColor();
                gameEventListener.onPieceMoved(promotionPosition, pawn);
                List<ChessPiece> chessPiecesAt = gameStatusListener.getChessPiecesAt(promotionPosition);
                for (ChessPiece chessPiece : chessPiecesAt) {
                    gameStatusListener.removeChessPiece(chessPiece);
                }
                ChessPiece newPiece = createNewPiece(color, promotionPosition, promotionType);
                gameEventListener.onPieceMoved(promotionPosition, newPiece);
                gameStatusListener.addChessPiece(newPiece);
                Player currentPlayer = gameTurnListener.getCurrentPlayer();
                if (currentPlayer.getColor() == Color.WHITE) {
                    gameTurnListener.setPlayerTurn("jake");
                    gameTurnListener.nextTurn();
                } else {
                    gameTurnListener.setPlayerTurn("pin");
                    gameTurnListener.nextTurn();
                }
            }
        });
    }

    private JButton createPromotionButton(String label, Type type, JDialog dialog){
        JButton button = new JButton(label);
        button.addActionListener(e -> {
            promotionType = type;
            dialog.dispose();
        });
        return button;
    }


    private boolean canPromote(ChessPiece pawn, Position position) {
        boolean isLastRank = position.y() == 0 || position.y() == 7;
        return pawn.getType() == Type.PAWN && isLastRank;
    }

    private ChessPiece createNewPiece(Color color, Position position, Type newType) {
        // 새 말 생성 로직, newType에 따라 적절한 말 객체를 생성하고 반환
        return new ChessPiece(newType, position, color);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()== rookButton){
            promotionType = Type.ROOK;
        }
        else if(e.getSource()== bishopButton){
            promotionType = Type.BISHOP;
        }
        else if(e.getSource()== knightButton){
            promotionType = Type.KNIGHT;
        }
        else if(e.getSource()== queenButton){
            promotionType = Type.QUEEN;
        }
    }
}