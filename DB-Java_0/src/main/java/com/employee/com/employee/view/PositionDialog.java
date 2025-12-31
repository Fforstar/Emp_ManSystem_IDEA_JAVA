package com.employee.view;

import com.employee.model.Position;
import javax.swing.*;
import java.awt.*;

public class PositionDialog extends JDialog {
    private JTextField nameField;
    private JTextField levelField;
    private JTextField salaryField;
    private JButton okButton;
    private JButton cancelButton;

    private Position position;
    private boolean confirmed = false;

    public PositionDialog(Frame owner, String title, Position position) {
        super(owner, title, true);
        this.position = position;
        initializeUI();
        loadData();
        setupEventHandlers();
    }

    private void initializeUI() {
        setSize(400, 250);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 表单
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        formPanel.add(new JLabel("职位名称*："));
        nameField = new JTextField(20);
        formPanel.add(nameField);

        formPanel.add(new JLabel("职级："));
        levelField = new JTextField(10);
        formPanel.add(levelField);

        formPanel.add(new JLabel("基础薪资*："));
        salaryField = new JTextField(15);
        formPanel.add(salaryField);

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
        if (position != null) {
            nameField.setText(position.getPosName());
            levelField.setText(position.getPosLevel() > 0 ? String.valueOf(position.getPosLevel()) : "");
            salaryField.setText(String.valueOf(position.getBasicSalary()));
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
            JOptionPane.showMessageDialog(this, "职位名称不能为空！");
            return false;
        }

        if (salaryField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "基础薪资不能为空！");
            return false;
        }

        try {
            Double.parseDouble(salaryField.getText());
            if (!levelField.getText().isEmpty()) {
                Integer.parseInt(levelField.getText());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "数字格式错误！");
            return false;
        }

        return true;
    }

    public Position getPosition() {
        if (position == null) {
            position = new Position();
        }

        position.setPosName(nameField.getText().trim());

        if (!levelField.getText().isEmpty()) {
            position.setPosLevel(Integer.parseInt(levelField.getText().trim()));
        }

        position.setBasicSalary(Double.parseDouble(salaryField.getText().trim()));

        return position;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}