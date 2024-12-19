package org.main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Registration extends JFrame {
    static String url = "jdbc:mysql://10.180.244.10:33306/Homework";
    static String user = "guest";
    static String passwordDB = "12345678";
    private JPanel mainPanel;
    private JRadioButton studentRadioButton,teacherRadioButton; //身份选择
    private JTextField studentIdField,nameField,passwdField,subjectField; //输入框
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

        // 姓名
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        JLabel passwdLabel = new JLabel("密码:");
        mainPanel.add(passwdLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        passwdField = new JTextField(20);
        mainPanel.add(passwdField, gbc);

        // 任教科目（仅老师）
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        JLabel subjectLabel = new JLabel("任教科目:");
        subjectLabel.setVisible(false);
        mainPanel.add(subjectLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        subjectField = new JTextField(20);
        subjectField.setVisible(false);
        mainPanel.add(subjectField, gbc);

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
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        registerButton = new JButton("注册");
        mainPanel.add(registerButton, gbc);

        // 返回按钮
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        backButton = new JButton("返回登录界面");
        mainPanel.add(backButton, gbc);

        // 添加事件监听器
        studentRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                studentIdField.setVisible(true);
                subjectLabel.setVisible(false);
                subjectField.setVisible(false);
                studentIdField.requestFocus();
                studentIdField.setText("");
            }
        });

        teacherRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                studentIdField.setVisible(true);
                subjectLabel.setVisible(true);
                subjectField.setVisible(true);
                studentIdField.requestFocus();
                subjectField.setText("");
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String role = studentRadioButton.isSelected() ? "学生" : "老师";
                String id = studentIdField.getText();
                String name = nameField.getText();
                String gender = (String) genderComboBox.getSelectedItem();
                String subject = teacherRadioButton.isSelected() ? subjectField.getText() : "";

                if (id.isEmpty() || name.isEmpty() || gender.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请填写完整信息！");
                } else {
                    // 注册成功
                    // 待会写一个发送数据到数据库
                    JOptionPane.showMessageDialog(null, role + " " + name + " 注册成功！");
                    // 返回登录界面
                    SwingUtilities.invokeLater(() -> new LoginFrame());
                    dispose();
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
    private void sendData(){

    }
}
