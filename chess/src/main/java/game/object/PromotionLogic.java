package game.object;

import game.Position;
import game.core.Color;
import game.core.GameTurnListener;
import game.factory.ChessPiece;
import game.factory.Type;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PromotionLogic implements ActionListener {

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
        if (canPromote(pawn, promotionPosition) && !promotedPawns.contains(pawn) && gameStatusListener != null){
            //promotion의 선택지를 주기
            showAndSelectType(pawn, promotionPosition);
            promotedPawns.add(pawn);

        }else if(gameStatusListener == null || gameEventListener == null) {
            throw new IllegalStateException("Listeners must not be null");
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

            // 프로모션 유형 선택을 위한 버튼 생성 및 추가
            JButton queenButton = new JButton("Queen");
            JButton rookButton = new JButton("Rook");
            JButton bishopButton = new JButton("Bishop");
            JButton knightButton = new JButton("Knight");

            // 각 버튼에 대한 액션 리스너 설정
            ActionListener actionListener = e -> {
                JButton source = (JButton) e.getSource();
                if (source == queenButton) {
                    promotionType = Type.QUEEN;
                } else if (source == rookButton) {
                    promotionType = Type.ROOK;
                } else if (source == bishopButton) {
                    promotionType = Type.BISHOP;
                } else if (source == knightButton) {
                    promotionType = Type.KNIGHT;
                }
                dialog.dispose(); // 사용자가 선택을 완료하면 대화상자를 닫습니다.
            };

            queenButton.addActionListener(actionListener);
            rookButton.addActionListener(actionListener);
            bishopButton.addActionListener(actionListener);
            knightButton.addActionListener(actionListener);

            // 버튼을 대화상자에 추가
            dialog.add(queenButton);
            dialog.add(rookButton);
            dialog.add(bishopButton);
            dialog.add(knightButton);

            dialog.setLocationRelativeTo(null); // 대화상자를 화면 중앙에 위치시킵니다.
            dialog.setVisible(true); // 대화상자를 보여줍니다.

            System.out.println(promotionType +"이게 프로모션타입이다이;다");
            if (gameStatusListener != null) {
                Optional<ChessPiece> chessPieceAt = gameStatusListener.getChessPieceAt(promotionPosition);
                chessPieceAt.ifPresent(gameStatusListener::removeChessPiece);
                ChessPiece newPiece = createNewPiece(pawn.getColor(), promotionPosition, promotionType);
                gameStatusListener.removeChessPiece(pawn);
                ChessPiece chessPiece = chessPieceAt.get();
                System.out.println("chessPiece : " + chessPiece.getType()+ " " + chessPiece.getColor() + " " + chessPiece.getPosition());
                gameEventListener.onPieceMoved(newPiece.getPosition(), newPiece);
                gameStatusListener.addChessPiece(newPiece);
                gameTurnListener.nextTurn();
            }
        });
    }


    private boolean canPromote(ChessPiece pawn, Position position) {
        // 폰이 마지막 랭크에 도달했는지 확인
        return (pawn.getType() == Type.PAWN) && (position.y() == 0 || position.y() == 7);
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