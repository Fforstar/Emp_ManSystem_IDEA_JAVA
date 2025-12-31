package com.employee.view;

import com.employee.model.Department;
import javax.swing.*;
import java.awt.*;

public class DepartmentDialog extends JDialog {
    private JTextField nameField;
    private JButton okButton;
    private JButton cancelButton;

    private Department department;
    private boolean confirmed = false;

    public DepartmentDialog(Frame owner, String title, Department department) {
        super(owner, title, true);
        this.department = department;
        initializeUI();
        loadData();
        setupEventHandlers();
    }

    private void initializeUI() {
        setSize(350, 200);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 表单
        JPanel formPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        formPanel.add(new JLabel("部门名称*："));
        nameField = new JTextField(20);
        formPanel.add(nameField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        okButton = new JButton("确定");
        cancelButton = new JButton("取消");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void loadData() {
        if (department != null) {
            nameField.setText(department.getDeptName());
        }
    }

    private void setupEventHandlers() {
        okButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "部门名称不能为空！");
            return false;
        }
        return true;
    }

    public Department getDepartment() {
        if (department == null) {
            department = new Department();
        }
        department.setDeptName(nameField.getText().trim());
        return department;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}