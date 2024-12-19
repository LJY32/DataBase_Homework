package org.main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class StudentModeFrame extends JFrame{
    private JButton queryButton,chooseButton,quitButton;
    private String stud_name;

    public StudentModeFrame(String stud_name) {
        this.stud_name = stud_name;
        setTitle("功能选择");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));
        queryButton = new JButton("查询成绩");
        chooseButton = new JButton("选课");
        quitButton = new JButton("退出");
        add(queryButton);
        add(chooseButton);
        add(quitButton);

        queryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                query();
            }
        });
        chooseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseClass();
            }
        });
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quit();
            }
        });
        setVisible(true);
    }

    public int getStudID(){
        int userId = -1;
        try (Connection conn = DriverManager.getConnection(LoginFrame.url, LoginFrame.user, LoginFrame.passwordDB);){
            ResultSet rs;
            // 查询user_id
            String queryUserId = "SELECT user_id FROM students WHERE student_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(queryUserId);
            pstmt.setString(1, stud_name);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("user_id");
                return userId;
            } else {
                JOptionPane.showMessageDialog(this, "未找到该学生！");
                return userId;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "查询失败：" + e.getMessage());
            return userId;
        }
    }

    private void query(){
        new GradeQuery(getStudID()).setVisible(true);
        dispose();
    }
    private void chooseClass(){
        new CourseSelection(getStudID());
        dispose();
    }
    private void quit(){
        dispose();
    }
}
