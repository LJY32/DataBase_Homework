package org.main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Registration extends JFrame {
    static String url = "jdbc:mysql://10.180.244.10:33306/Homework";
    static String user = "root";
    static String passwordDB = "12345678";
    private JPanel mainPanel;
    private JRadioButton studentRadioButton,teacherRadioButton; //身份选择
    private JTextField studentIdField,nameField; //输入框
    private  JPasswordField passwdField,passwd2Field;
    private JComboBox<String> genderComboBox;
    private JButton registerButton,backButton; //按钮

    public Registration() {
        // 设置窗口标题
        setTitle("注册窗口");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 初始化组件
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 身份选择
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel roleLabel = new JLabel("身份选择:");
        mainPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        studentRadioButton = new JRadioButton("学生");
        teacherRadioButton = new JRadioButton("老师");
        ButtonGroup group = new ButtonGroup();
        group.add(studentRadioButton);
        group.add(teacherRadioButton);
        mainPanel.add(studentRadioButton, gbc);
        gbc.gridx = 2;
        mainPanel.add(teacherRadioButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        JLabel idLabel = new JLabel("学工号:");
        mainPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        studentIdField = new JTextField(20);
        studentIdField.setVisible(true);
        mainPanel.add(studentIdField, gbc);

        // 姓名
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        JLabel nameLabel = new JLabel("姓名:");
        mainPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        nameField = new JTextField(20);
        mainPanel.add(nameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        JLabel passwdLabel = new JLabel("密码:");
        mainPanel.add(passwdLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        passwdField = new JPasswordField(20);
        mainPanel.add(passwdField, gbc);

        // 确认密码
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        JLabel passwd2Label = new JLabel("确认密码:");
        mainPanel.add(passwd2Label, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        passwd2Field = new JPasswordField(20);
        mainPanel.add(passwd2Field, gbc);

        // 性别
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        JLabel genderLabel = new JLabel("性别:");
        mainPanel.add(genderLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        String[] genderOptions = {"男", "女"};
        genderComboBox = new JComboBox<>(genderOptions);
        mainPanel.add(genderComboBox, gbc);

        // 注册按钮
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        registerButton = new JButton("注册");
        mainPanel.add(registerButton, gbc);

        // 返回按钮
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        backButton = new JButton("返回登录界面");
        mainPanel.add(backButton, gbc);

        // 添加事件监听器
        studentRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                studentIdField.setVisible(true);
                studentIdField.requestFocus();
                studentIdField.setText("");
            }
        });

        teacherRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                studentIdField.setVisible(true);
                studentIdField.requestFocus();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String role = studentRadioButton.isSelected() ? "学生" : "老师";
                String id = studentIdField.getText();
                String name = nameField.getText();
                String passwd = passwdField.getText();
                String passwdConfirm = passwd2Field.getText();
                String gender = (String) genderComboBox.getSelectedItem();
                int usertype;
                if (id.isEmpty() || name.isEmpty() || gender.isEmpty() || passwd.isEmpty() || !studentRadioButton.isSelected())
                    JOptionPane.showMessageDialog(null, "请填写完整信息！");
                else {
                    if (!passwd.equals(passwdConfirm))
                        JOptionPane.showMessageDialog(null, "两次输入密码不相同！");
                    else {
                        if (role == "学生") usertype = 1;
                        else usertype = 0;
                        sendData(name, passwd, usertype);
                        JOptionPane.showMessageDialog(null, role + " " + name + " 注册成功！");
                        // 返回登录界面
                        SwingUtilities.invokeLater(() -> new LoginFrame());
                        dispose();
                    }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> new LoginFrame());
                dispose();
            }
        });
        add(mainPanel);
    }
    private static void sendData(String username,String password, int usertype){
        String sql = "INSERT INTO users (username, password, usertype) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, passwordDB);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3,usertype);

            pstmt.executeUpdate();
            System.out.printf("注册成功！\n用户名：%s\n用户类型：%d",username,usertype);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
