package org.main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginFrame extends JFrame {
    static String url = "jdbc:mysql://10.180.244.10:33306/Homework";
    static String user = "root";
    static String passwordDB = "12345678";
    private JTextField usernameField;
    private JPasswordField passwordField; //这个输入框可以让密码变成点点点
    private JButton loginButton,quitButton;
    private JLabel usernameLabel,passwordLabel;

    public LoginFrame() {
        setTitle("登录界面");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        usernameLabel = new JLabel("用户名:");
        passwordLabel = new JLabel("密码:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("登录");
        quitButton = new JButton("退出");

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(quitButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quit();
            }
        });
        setVisible(true);
    }
    private void quit(){
        dispose();
    }
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DriverManager.getConnection(url, user, passwordDB);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (password.equals(storedPassword)) {
                    JOptionPane.showMessageDialog(this, "登录成功！");
                    SwingUtilities.invokeLater(() -> new ModeChooseFrame()); //进入模式选择窗口
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "密码错误！");
                }
            } else {
                JOptionPane.showMessageDialog(this, "用户名错误！");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库连接失败！");
        }
    }
}