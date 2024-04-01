package game.login;

import javax.swing.*;
import java.awt.*;
public class RegisterFrame extends  JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JTextField emailField;

    public RegisterFrame() {
        setTitle("Register Form");
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 사용자 이름 입력 필드
        JPanel idPanel = new JPanel();
        usernameField = new JTextField(15);
        idPanel.add(new JLabel("Username:"));
        idPanel.add(usernameField);
        JButton idDuplicateCheckButton = new JButton("Check ID");
        idPanel.add(idDuplicateCheckButton);
        idDuplicateCheckButton.addActionListener(e -> {
            String username = usernameField.getText();
            String message = new MessageBuilder().add("username", username).buildJson();
            // 아이디 중복 확인 요청 처리
            HttpClient.sendIdDuplicateCheckRequest(message);
        });
        add(idPanel);

        JPanel passwordPanel = new JPanel();
        // 비밀번호 입력 필드
        passwordField = new JPasswordField(15);
        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(passwordField);
        add(passwordPanel);

        // 이메일 입력 필드
        JPanel emailPanel = new JPanel();
        emailField = new JTextField(15);
        emailPanel.add(new JLabel("       Email:"));
        emailPanel.add(emailField);
        add(emailPanel);

        // 회원가입 버튼
        JPanel buttonPanel = getjPanel();
        add(buttonPanel);
        setVisible(true);
    }

    private JPanel getjPanel() {
        JPanel buttonPanel = new JPanel();
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            String message = new MessageBuilder().add("username", usernameField.getText())
                    .add("password", passwordField.getPassword())
                    .add("email", emailField.getText())
                    .buildJson();
            // 회원가입 요청 처리
            // 예: HttpClient 클래스의 sendMessage 메서드를 사용하여 Django 서버로 회원가입 요청을 보냄
            HttpClient.sendRegisterRequest(message);
        });
        buttonPanel.add(registerButton);
        return buttonPanel;
    }
}
