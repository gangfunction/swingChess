package game.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerFrame extends JFrame implements ActionListener {
    private  JButton refreshButton;
    private  JButton joinButton;
    private  JButton createButton;
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
            JOptionPane.showMessageDialog(this, "Joining room " + selectedRoom);
        }
        else{
            JOptionPane.showMessageDialog(this, "Please select a room to join");
        }
        
    }

    private void refreshRoomList() {
        listModel.clear();
        listModel.addElement("Room 1");
        listModel.addElement("Room 2");
        listModel.addElement("Room 3");
    }

}
