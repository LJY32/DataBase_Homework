package org.main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentModeFrame extends JFrame{
    private JButton queryButton,quitButton;

    public StudentModeFrame() {
        setTitle("功能选择");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));
        queryButton = new JButton("查询成绩");
        quitButton = new JButton("退出");
        add(queryButton);
        add(quitButton);

        queryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                query();
            }
        });
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quit();
            }
        });
        setVisible(true);
    }
    private void query(){
        //明天再写
        dispose();
    }
    private void quit(){
        dispose();
    }
}
