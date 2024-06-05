package game.login;

import lombok.Getter;
import lombok.Setter;

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

    @Getter
    @Setter
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
