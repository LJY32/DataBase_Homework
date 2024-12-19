package org.main;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GradeQuery extends JFrame {
    private JTable gradeTable;
    private DefaultTableModel tableModel;

    public GradeQuery(int stud_name) {
        setTitle("成绩查询");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建面板
        JPanel panel = new JPanel(new GridLayout(2, 2));

        // 添加查询按钮
        JButton queryButton = new JButton("查询成绩");
        queryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                queryGrades(stud_name);
            }
        });
        panel.add(queryButton);

        // 创建表格
        String[] columnNames = {"课程", "成绩"};
        tableModel = new DefaultTableModel(columnNames, 0);
        gradeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        panel.add(scrollPane);

        // 添加面板到框架
        add(panel);
    }

    private void queryGrades(int userId) {
        String url = "jdbc:mysql://10.180.244.10:33306/Homework";
        String user = "root";
        String password = "12345678";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        ResultSet crs = null;

        try {
            // 建立连接
            conn = DriverManager.getConnection(url, user, password);
            // 创建Statement对象
            stmt = conn.createStatement();

            // 查询成绩
            String queryGrades = "SELECT stud_course_id, stud_course_grade FROM stud_grade WHERE stud_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(queryGrades);
            pstmt = conn.prepareStatement(queryGrades);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            // 清空表格模型
            tableModel.setRowCount(0);

            // 添加数据到表格
            while (rs.next()) {
                int grade = rs.getInt("stud_course_grade"),
                    course_id = rs.getInt("stud_course_id");
                String queryName = "SELECT course_name FROM course WHERE course_id = ?";
                PreparedStatement cpstmt;
                cpstmt = conn.prepareStatement(queryName);
                cpstmt.setInt(1, course_id);
                crs = cpstmt.executeQuery();
                crs.next();
                String course_name = crs.getString("course_name");
                tableModel.addRow(new Object[]{course_name, grade});
            }

            JOptionPane.showMessageDialog(this, "成绩查询成功！");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "查询失败：" + e.getMessage());
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}