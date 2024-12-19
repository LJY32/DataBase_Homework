package org.main;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashSet;

public class CourseSelection {
    private JFrame frame;
    private JTable courseTable;
    private JComboBox<String> selectionComboBox;
    private DefaultTableModel tableModel;
    private Connection connection;

    // 数据库连接信息
    private static final String DB_URL = "jdbc:mysql://10.180.244.10:33306/Homework";
    private static final String USER = "root";
    private static final String PASS = "12345678";
    private int stud_id;

    public CourseSelection(int stud_id) {
        this.stud_id = stud_id;
        try {
            // 初始化数据库连接
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库连接失败！", "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // 初始化窗口
        frame = new JFrame("课程选择");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // 初始化表格
        String[] columnNames = {"课程ID", "课程名", "选择"};
        tableModel = new DefaultTableModel(columnNames, 0);
        courseTable = new JTable(tableModel);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.getColumnModel().getColumn(2).setCellEditor(null); // 禁止编辑第三列

        // 添加表格选择监听器
        courseTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateSelectionComboBox();
            }
        });

        // 初始化单选按钮组
        JPanel panel = new JPanel();
        String[] comboBoxOptions = {"未选", "已选"};
        selectionComboBox = new JComboBox<>(comboBoxOptions);
        selectionComboBox.setEnabled(false); // 初始化为不可用
        panel.add(selectionComboBox);

        // 初始化提交按钮
        JButton submitButton = new JButton("提交");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitSelection();
            }
        });
        panel.add(submitButton);

        // 添加组件到窗口
        frame.add(new JScrollPane(courseTable), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.EAST);

        // 加载数据
        loadData();

        // 显示窗口
        frame.setVisible(true);
    }

    // 加载课程数据
    private void loadData() {
        try (
            PreparedStatement  ps = connection.prepareStatement("SELECT course_id FROM stud_course WHERE stud_id = ?");
            Statement stmt = connection.createStatement();
            ResultSet courseRs = stmt.executeQuery("SELECT course_id, course_name FROM course");
            ){
            ps.setInt(1,stud_id);
            ResultSet selectedCourseRs = ps.executeQuery();
            // 清空表格模型
            tableModel.setRowCount(0);

            // 存储已选课程ID
            HashSet<String> selectedCourseIds = new HashSet<>();
            while (selectedCourseRs.next()) {
                selectedCourseIds.add(selectedCourseRs.getString("course_id"));
            }

            // 填充表格
            while (courseRs.next()) {
                String courseId = courseRs.getString("course_id");
                String courseName = courseRs.getString("course_name");
                String selected = selectedCourseIds.contains(courseId) ? "已选" : "未选";

                Object[] row = {courseId, courseName, selected};
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "加载数据失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 更新单选按钮组
    private void updateSelectionComboBox() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow != -1) {
            String selected = (String) tableModel.getValueAt(selectedRow, 2);
            if ("未选".equals(selected)) {
                selectionComboBox.setEnabled(true);
            } else {
                selectionComboBox.setEnabled(false);
            }
        } else {
            selectionComboBox.setEnabled(false);
            selectionComboBox.setSelectedIndex(0);
        }
    }

    // 提交选择
    private void submitSelection() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow != -1 && selectionComboBox.getSelectedIndex() == 1) {
            try (PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO stud_course (stud_id, course_id) VALUES (?, ?)")) {

                String courseId = (String) tableModel.getValueAt(selectedRow, 0);
                pstmt.setInt(1, stud_id);
                pstmt.setString(2, courseId);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "选课成功！", "信息", JOptionPane.INFORMATION_MESSAGE);

                // 重新加载数据
                loadData();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "提交数据失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择未选的课程进行提交！", "警告", JOptionPane.WARNING_MESSAGE);
        }
    }
}