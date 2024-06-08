package game.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URISyntaxException;

public class LoginFrame extends JFrame implements ActionListener {
    private final JTextField userTextField;
    private final JTextField passwordField;
    private final JButton loginButton;
    private final JButton registerButton;
    private final JButton offlineButton;

    public LoginFrame() {
        setTitle("Login Form");
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        // 사용자 이름 입력 필드
        JPanel namePanel = new JPanel();
        userTextField = new JTextField(20);
        namePanel.add(new JLabel("Username:"));
        namePanel.add(userTextField);
        add(namePanel);

        // 비밀번호 입력 필드
        JPanel passwordPanel = new JPanel();
        passwordField = new JPasswordField(20);
        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(passwordField);
        add(passwordPanel);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        // 로그인 버튼
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        panel.add(loginButton);


        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        panel.add(registerButton);
        add(panel);

        offlineButton = new JButton("Offline");
        offlineButton.addActionListener(this);
        panel.add(offlineButton);
        add(panel);

        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String message = new MessageBuilder().add("username", userTextField.getText())
                    .add("password", passwordField.getText())
                    .buildJson();
            HttpClient.sendLoginRequest(message);
            this.dispose();
        } else if (e.getSource() == registerButton) {
            // 회원가입 창으로 이동
            new RegisterFrame();
            this.dispose();
        }
        else if (e.getSource() == offlineButton) {
            // 오프라인 모드로 이동
            JOptionPane.showMessageDialog(null, "Offline mode");
            this.dispose();
        }
    }
}

