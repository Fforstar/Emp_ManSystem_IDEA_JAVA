package com.employee.view;

import com.employee.model.Employee;
import com.employee.model.Salary;
import com.employee.service.EmployeeService;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SalaryDialog extends JDialog {
    private JComboBox<Employee> employeeCombo;
    private JTextField monthField;
    private JTextField basicField;
    private JTextField bonusField;
    private JTextField deductionField;
    private JButton okButton;
    private JButton cancelButton;

    private Salary salary;
    private boolean confirmed = false;

    private EmployeeService employeeService = new EmployeeService();
    private SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");

    public SalaryDialog(Frame owner, String title, Salary salary) {
        super(owner, title, true);
        this.salary = salary;
        initializeUI();
        loadEmployees();
        loadData();
        setupEventHandlers();
    }

    private void initializeUI() {
        setSize(400, 350);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 表单
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        formPanel.add(new JLabel("员工*："));
        employeeCombo = new JComboBox<>();
        formPanel.add(employeeCombo);

        formPanel.add(new JLabel("月份*："));
        monthField = new JTextField(monthFormat.format(new Date()));
        formPanel.add(monthField);

        formPanel.add(new JLabel("基本工资*："));
        basicField = new JTextField();
        formPanel.add(basicField);

        formPanel.add(new JLabel("奖金："));
        bonusField = new JTextField("0");
        formPanel.add(bonusField);

        formPanel.add(new JLabel("扣款："));
        deductionField = new JTextField("0");
        formPanel.add(deductionField);

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

    private void loadEmployees() {
        employeeCombo.removeAllItems();
        List<Employee> employees = employeeService.getAllEmployees();
        for (Employee emp : employees) {
            employeeCombo.addItem(emp);
        }
    }

    private void loadData() {
        if (salary != null) {
            // 选中员工
            for (int i = 0; i < employeeCombo.getItemCount(); i++) {
                if (employeeCombo.getItemAt(i).getEmpId() == salary.getEmpId()) {
                    employeeCombo.setSelectedIndex(i);
                    break;
                }
            }

            monthField.setText(monthFormat.format(salary.getSalaryMonth()));
            basicField.setText(String.valueOf(salary.getBasicSalary()));
            bonusField.setText(String.valueOf(salary.getBonus()));
            deductionField.setText(String.valueOf(salary.getDeduction()));
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
        if (basicField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入基本工资！");
            return false;
        }

        try {
            Double.parseDouble(basicField.getText());
            Double.parseDouble(bonusField.getText());
            Double.parseDouble(deductionField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "金额格式错误！");
            return false;
        }

        return true;
    }

    public Salary getSalary() {
        if (salary == null) {
            salary = new Salary();
        }

        Employee emp = (Employee) employeeCombo.getSelectedItem();
        if (emp != null) {
            salary.setEmpId(emp.getEmpId());
        }

        try {
            salary.setSalaryMonth(monthFormat.parse(monthField.getText()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        salary.setBasicSalary(Double.parseDouble(basicField.getText()));
        salary.setBonus(Double.parseDouble(bonusField.getText()));
        salary.setDeduction(Double.parseDouble(deductionField.getText()));
        salary.calculateNetSalary();

        return salary;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}