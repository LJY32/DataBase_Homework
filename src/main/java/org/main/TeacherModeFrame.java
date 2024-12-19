package org.main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TeacherModeFrame extends JFrame{
    private JButton queryButton,chooseButton,quitButton;
    private String teac_name;

    public TeacherModeFrame(String teac_name) {
        this.teac_name = teac_name;
        setTitle("功能选择");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));
        queryButton = new JButton("登记成绩");
        chooseButton = new JButton("选择执教课程");
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
                courseSelector();
            }
        });
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quit();
            }
        });
        setVisible(true);
    }

    public int getTeacID(){
        int userId = -1;
        try (Connection conn = DriverManager.getConnection(LoginFrame.url, LoginFrame.user, LoginFrame.passwordDB);){
            ResultSet rs;
            // 查询user_id
            String queryUserId = "SELECT user_id FROM teachers WHERE teacher_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(queryUserId);
            pstmt.setString(1, teac_name);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("user_id");
                return userId;
            } else {
                JOptionPane.showMessageDialog(this, "未找到老师！");
                return userId;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "查询失败：" + e.getMessage());
            return userId;
        }
    }

    private void query(){
        new GradeQuery(getTeacID()).setVisible(true);
        dispose();
    }
    private void courseSelector(){
        new TeacherCourseSelector(getTeacID());
        dispose();
    }
    private void quit(){
        dispose();
    }
}
