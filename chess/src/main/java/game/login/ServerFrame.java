package game.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerFrame extends JFrame implements ActionListener {
    private final JButton refreshButton;
    private final JButton joinButton;
    private final JButton createButton;
    private final JList<String> roomList;
    private final DefaultListModel<String> listModel;

    public boolean isMaxRoom() {
        return MaxRoom;
    }

    public void setMaxRoom(boolean maxRoom) {
        MaxRoom = maxRoom;
    }

    private boolean MaxRoom = false;
    
    public ServerFrame() {
        setTitle("Game Room List");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        roomList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(roomList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(this);
        buttonPanel.add(refreshButton);

        joinButton = new JButton("Join");
        joinButton.addActionListener(this);
        buttonPanel.add(joinButton);

        createButton = new JButton("Create");
        createButton.addActionListener(this);
        buttonPanel.add(createButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refreshButton) {
            refreshRoomList();
        }
        if (e.getSource() == joinButton) {
            joinRoom();
            this.dispose();
        }
        if (e.getSource() == createButton) {
            createRoom();
        }
    }

    private void createRoom() {
        String roomName = JOptionPane.showInputDialog("Enter room name");
        String message = new MessageBuilder().add("room_name", roomName).buildJson();
        HttpClient.sendCreateRoomRequest(message);
        if (roomName != null && !roomName.isEmpty()) {
            listModel.addElement(roomName);
        }
    }

    /*
      1. db로직으로 참가하는 사람의 의사를 백엔드에 보낸다.
      2. 백엔드는 받아서 저장한다.
      3. 방의 상태를 백엔드에서 업데이트한다.
      4. 백엔드는 참가자에게 응답을 보낸다.
      5. 응답에 방의 상태와 참가자의 정보를 보낸다.
      6. 참가자는 방의 상태를 업데이트한다.
      7. 참가자는 게임을 시작한다.
     */
    private void joinRoom() {
        String selectedRoom = roomList.getSelectedValue();
        if (selectedRoom != null) {
            JOptionPane.showMessageDialog(this, "Try to Joining room " + selectedRoom);
            HttpClient.sendJoinRoomRequest(selectedRoom);
        }
        else{
            JOptionPane.showMessageDialog(this, "Please select a room to join");
            new ServerFrame();
        }
        
    }

    private void refreshRoomList() {
        listModel.clear();
        HttpClient.sendRoomListRequest();
        /*
            1. 백엔드에 방 목록 조회를 요청하는 메시지 객체를 생성한다.
            2. 백엔드는 방 목록을 응답한다.
            3. 응답을 받은 프론트는 방 목록을 업데이트한다.
         */
    }

}
