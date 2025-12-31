package com.employee.view;

import com.employee.model.Attendance;
import com.employee.model.Employee;
import com.employee.service.EmployeeService;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class AttendanceDialog extends JDialog {
    private JComboBox<Employee> employeeCombo;
    private JTextField dateField;
    private JTextField checkInField;
    private JTextField checkOutField;
    private JButton okButton;
    private JButton cancelButton;

    private Attendance attendance;
    private boolean confirmed = false;

    private EmployeeService employeeService = new EmployeeService();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public AttendanceDialog(Frame owner, String title, Attendance attendance) {
        super(owner, title, true);
        this.attendance = attendance;
        initializeUI();
        loadEmployees();
        loadData();
        setupEventHandlers();
    }

    private void initializeUI() {
        setSize(400, 300);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 表单
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        formPanel.add(new JLabel("员工*："));
        employeeCombo = new JComboBox<>();
        formPanel.add(employeeCombo);

        formPanel.add(new JLabel("日期*："));
        dateField = new JTextField(dateFormat.format(new java.util.Date()));
        formPanel.add(dateField);

        formPanel.add(new JLabel("签到时间："));
        checkInField = new JTextField("09:00:00");
        formPanel.add(checkInField);

        formPanel.add(new JLabel("签退时间："));
        checkOutField = new JTextField("18:00:00");
        formPanel.add(checkOutField);

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
        if (attendance != null) {
            // 选中员工
            for (int i = 0; i < employeeCombo.getItemCount(); i++) {
                if (employeeCombo.getItemAt(i).getEmpId() == attendance.getEmpId()) {
                    employeeCombo.setSelectedIndex(i);
                    break;
                }
            }

            dateField.setText(dateFormat.format(attendance.getAttDate()));
            checkInField.setText(timeFormat.format(attendance.getCheckIn()));
            checkOutField.setText(timeFormat.format(attendance.getCheckOut()));
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
        if (dateField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入日期！");
            return false;
        }

        if (checkInField.getText().isEmpty() && checkOutField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "签到和签退时间至少填写一个！");
            return false;
        }

        return true;
    }

    public Attendance getAttendance() {
        if (attendance == null) {
            attendance = new Attendance();
        }

        Employee emp = (Employee) employeeCombo.getSelectedItem();
        if (emp != null) {
            attendance.setEmpId(emp.getEmpId());
        }

        try {
            attendance.setAttDate(dateFormat.parse(dateField.getText()));

            if (!checkInField.getText().isEmpty()) {
                attendance.setCheckIn(java.sql.Time.valueOf(checkInField.getText()));
            }

            if (!checkOutField.getText().isEmpty()) {
                attendance.setCheckOut(java.sql.Time.valueOf(checkOutField.getText()));
            }

            attendance.setStatus("出勤");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attendance;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}