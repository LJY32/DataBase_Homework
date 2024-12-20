package org.main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherScore extends JFrame {
    private int teacher_id;
    private JTable studentGradeTable;
    private DefaultTableModel tableModel;
    private Connection connection;

    public TeacherScore(int teacher_id) {
        this.teacher_id = teacher_id;
        // 设置窗口标题
        setTitle("教师打分窗口");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建布局和组件
        setLayout(new BorderLayout());

        // 顶部面板：教师ID输入框和提交按钮
        JPanel topPanel = new JPanel(new FlowLayout());
        add(topPanel, BorderLayout.NORTH);

        // 中间面板：学生成绩表格
        String[] columnNames = {"学生ID", "课程ID", "课程成绩"};
        tableModel = new DefaultTableModel(columnNames, 0);
        studentGradeTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(studentGradeTable);
        add(scrollPane, BorderLayout.CENTER);

        // 底部面板：保存按钮
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("保存成绩");

        bottomPanel.add(saveButton);
        add(bottomPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGrades();
            }
        });

        // 初始化数据库连接
        initDatabaseConnection();
    }

    private void initDatabaseConnection() {
        try {
            String url = "jdbc:mysql://10.180.244.10:33306/Homework";
            String user = "root";
            String password = "12345678";
            connection = DriverManager.getConnection(url, user, password);
            loadCoursesAndStudents();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库连接失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void loadCoursesAndStudents() {
        String teacherId = String.valueOf(teacher_id);
        if (teacherId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "教师ID出错", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 获取教师执教的课程ID
            List<Integer> courseIds = getCourseIdsByTeacherId(teacherId);

            // 清空表格
            tableModel.setRowCount(0);

            // 获取课程下的学生及成绩
            for (int courseId : courseIds) {
                List<StudentGrade> studentGrades = getStudentGradesByCourseId(courseId);
                for (StudentGrade studentGrade : studentGrades) {
                    tableModel.addRow(new Object[]{studentGrade.getStudId(), courseId, studentGrade.getGrade()});
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据加载失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Integer> getCourseIdsByTeacherId(String teacherId) throws SQLException {
        List<Integer> courseIds = new ArrayList<>();
        String query = "SELECT course_id FROM course WHERE course_t_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, teacherId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    courseIds.add(resultSet.getInt("course_id"));
                }
            }
        }
        return courseIds;
    }

    private List<StudentGrade> getStudentGradesByCourseId(int courseId) throws SQLException {
        List<StudentGrade> studentGrades = new ArrayList<>();
        String query = "SELECT stud_id, stud_course_grade FROM stud_grade WHERE stud_course_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int studId = resultSet.getInt("stud_id");
                    String grade = resultSet.getString("stud_course_grade");
                    studentGrades.add(new StudentGrade(studId, grade));
                }
            }
        }
        return studentGrades;
    }

    private void saveGrades() {
        try {
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                int studId = (int) tableModel.getValueAt(row, 0);
                int courseId = (int) tableModel.getValueAt(row, 1);
                String grade = (String) tableModel.getValueAt(row, 2);

                updateStudentGrade(studId, courseId, grade);
            }
            JOptionPane.showMessageDialog(this, "成绩保存成功", "信息", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "成绩保存失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudentGrade(int studId, int courseId, String grade) throws SQLException {
        String query = "UPDATE stud_grade SET stud_course_grade = ? WHERE stud_id = ? AND stud_course_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, grade);
            statement.setInt(2, studId);
            statement.setInt(3, courseId);
            statement.executeUpdate();
        }
    }

    private static class StudentGrade {
        private int studId;
        private String grade;

        public StudentGrade(int studId, String grade) {
            this.studId = studId;
            this.grade = grade != null ? grade : "";
        }

        public int getStudId() {
            return studId;
        }

        public String getGrade() {
            return grade;
        }
    }
}