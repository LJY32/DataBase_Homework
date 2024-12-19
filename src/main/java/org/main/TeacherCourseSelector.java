package org.main;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TeacherCourseSelector extends JFrame {

    private int teacherId;
    private JComboBox<String> departmentComboBox;
    private JTextField courseNameTextField;
    private JTable courseTable;
    private DefaultTableModel tableModel;

    public TeacherCourseSelector(int teacherId) {
        this.teacherId = teacherId;
        setTitle("教师选择执教课程");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Load departments into combo box
        loadDepartments();

        // Create UI components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("学院:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(departmentComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("课程名称:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        courseNameTextField = new JTextField(20);
        panel.add(courseNameTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addCourseButton = new JButton("添加执教课程");
        addCourseButton.addActionListener(new AddCourseActionListener());
        panel.add(addCourseButton, gbc);

        add(panel, BorderLayout.NORTH);

        // Load courses for the teacher
        loadCourses();

        // Create table to display courses
        tableModel = new DefaultTableModel(new String[]{"课程名称", "学院"}, 0);
        courseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add table selection listener to update course details
        courseTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = courseTable.getSelectedRow();
                    if (selectedRow != -1) {
                        // You can add code here to display more details about the selected course
                    }
                }
            }
        });

        setVisible(true);
    }

    private void loadDepartments() {
        String query = "SELECT d_name FROM department";
        departmentComboBox = new JComboBox<>();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.180.244.10:33306/Homework", "root", "12345678");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                departmentComboBox.addItem(rs.getString("d_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

private void loadCourses() {
    String query = "SELECT c.course_name, d.d_name FROM course c JOIN department d ON c.course__id = d.d_id WHERE c.course_t_id = ?";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.180.244.10:33306/Homework", "root", "12345678");
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, teacherId);
        ResultSet rs = stmt.executeQuery();

        tableModel.setRowCount(0); // 清空表格模型中的所有行
        while (rs.next()) {
            tableModel.addRow(new Object[]{rs.getString("course_name"), rs.getString("d_name")});
        }

    } catch (SQLException e) {

        e.printStackTrace();
    }
}
    private class AddCourseActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String department = (String) departmentComboBox.getSelectedItem();
            String courseName = courseNameTextField.getText().trim();

            if (department == null || department.isEmpty() || courseName == null || courseName.isEmpty()) {
                JOptionPane.showMessageDialog(TeacherCourseSelector.this, "请填写完整信息！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int departmentId = getDepartmentId(department);
            if (departmentId == -1) {
                JOptionPane.showMessageDialog(TeacherCourseSelector.this, "无效的学院！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String insertQuery = "INSERT INTO course (course_name, course_d_id, course_t_id) VALUES (?, ?, ?)";

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.180.244.10:33306/Homework", "root", "12345678");
                 PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

                stmt.setString(1, courseName);
                stmt.setInt(2, departmentId);
                stmt.setInt(3, teacherId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(TeacherCourseSelector.this, "课程添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

                loadCourses();

            } catch (SQLException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(TeacherCourseSelector.this, "添加课程失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }

        private int getDepartmentId(String departmentName) {
            String query = "SELECT d_id FROM department WHERE d_name = ?";

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://10.180.244.10:33306/Homework", "root", "12345678");
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, departmentName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt("d_id");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return -1;
        }
    }
}