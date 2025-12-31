package com.employee.view;

import com.employee.model.Department;
import com.employee.model.Employee;
import com.employee.model.Position;
import com.employee.service.DepartmentService;
import com.employee.service.PositionService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EmployeeDialog extends JDialog {
    // 表单字段
    private JTextField nameField = new JTextField(20);
    private JComboBox<String> genderBox = new JComboBox<>(new String[]{"男", "女"});
    private JTextField hireField = new JTextField();
    private JComboBox<Department> deptBox = new JComboBox<>();
    private JComboBox<Position> posBox = new JComboBox<>();
    private JTextArea addressArea = new JTextArea(3, 20);
    private JTextField phoneField = new JTextField(15);
    private JTextField emailField = new JTextField(20);

    // 按钮
    private JButton okButton = new JButton("确定");
    private JButton cancelButton = new JButton("取消");

    // 数据
    private Employee employee;
    private boolean confirmed = false;

    private DepartmentService departmentService = new DepartmentService();
    private PositionService positionService = new PositionService();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public EmployeeDialog(Frame owner, String title, Employee employee) {
        super(owner, title, true);
        this.employee = employee;

        initializeUI();
        loadDeptsAndPositions();
        loadData();
        setupEventHandlers();
    }

    private void initializeUI() {
        setSize(500, 450);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 添加表单行
        addFormRow(formPanel, gbc, 0, "姓名*：", nameField);
        addFormRow(formPanel, gbc, 1, "性别*：", genderBox);
        addFormRow(formPanel, gbc, 2, "入职日期*：", hireField);
        addFormRow(formPanel, gbc, 3, "部门：", deptBox);
        addFormRow(formPanel, gbc, 4, "职位：", posBox);
        addFormRow(formPanel, gbc, 5, "手机号：", phoneField);
        addFormRow(formPanel, gbc, 6, "邮箱：", emailField);

        // 地址（多行）
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("地址："), gbc);
        gbc.gridx = 1;
        JScrollPane scrollPane = new JScrollPane(addressArea);
        formPanel.add(scrollPane, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    private void loadDeptsAndPositions() {
        // 加载部门
        deptBox.removeAllItems();
        List<Department> depts = departmentService.getAllDepartments();
        for (Department dept : depts) {
            deptBox.addItem(dept);
        }

        // 加载职位
        posBox.removeAllItems();
        List<Position> positions = positionService.getAllPositions();
        for (Position pos : positions) {
            posBox.addItem(pos);
        }

        // 添加默认提示
        deptBox.insertItemAt(new Department("--请选择部门--", -1), 0);
        posBox.insertItemAt(new Position("--请选择职位--", -1), 0);
        deptBox.setSelectedIndex(0);
        posBox.setSelectedIndex(0);
    }

    private void loadData() {
        hireField.setText(dateFormat.format(new Date())); // 默认今天

        if (employee != null) {
            nameField.setText(employee.getEmpName());
            genderBox.setSelectedItem(employee.getGender());
            hireField.setText(dateFormat.format(employee.getHireDate()));
            phoneField.setText(employee.getPhone() != null ? employee.getPhone() : "");
            emailField.setText(employee.getEmail() != null ? employee.getEmail() : "");
            addressArea.setText(employee.getAddress() != null ? employee.getAddress() : "");

            // 选中部门和职位
            if (employee.getDeptId() > 0) {
                for (int i = 0; i < deptBox.getItemCount(); i++) {
                    if (deptBox.getItemAt(i).getDeptId() == employee.getDeptId()) {
                        deptBox.setSelectedIndex(i);
                        break;
                    }
                }
            }

            if (employee.getPosId() > 0) {
                for (int i = 0; i < posBox.getItemCount(); i++) {
                    if (posBox.getItemAt(i).getPosId() == employee.getPosId()) {
                        posBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
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
            JOptionPane.showMessageDialog(this, "姓名不能为空！", "验证错误", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        if (hireField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "入职日期不能为空！", "验证错误", JOptionPane.ERROR_MESSAGE);
            hireField.requestFocus();
            return false;
        }

        // 验证日期格式
        try {
            dateFormat.parse(hireField.getText().trim());
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "日期格式错误！应为: yyyy-MM-dd", "验证错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public Employee getEmployee() {
        if (employee == null) {
            employee = new Employee();
        }

        employee.setEmpName(nameField.getText().trim());
        employee.setGender(genderBox.getSelectedItem().toString());

        try {
            employee.setHireDate(dateFormat.parse(hireField.getText().trim()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        employee.setPhone(phoneField.getText().trim());
        employee.setEmail(emailField.getText().trim());
        employee.setAddress(addressArea.getText().trim());
        employee.setStatus("在职");

        // 处理部门和职位选择
        Department selectedDept = (Department) deptBox.getSelectedItem();
        if (selectedDept != null && selectedDept.getDeptId() > 0) {
            employee.setDeptId(selectedDept.getDeptId());
        } else {
            employee.setDeptId(0);
        }

        Position selectedPos = (Position) posBox.getSelectedItem();
        if (selectedPos != null && selectedPos.getPosId() > 0) {
            employee.setPosId(selectedPos.getPosId());
        } else {
            employee.setPosId(0);
        }

        return employee;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}