package com.employee.view;

import com.employee.model.Employee;
import com.employee.service.EmployeeService;
import com.employee.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserRegisterDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<Employee> employeeCombo;
    private JComboBox<String> roleCombo;
    private JButton registerButton;
    private JButton cancelButton;

    private UserService userService = new UserService();
    private EmployeeService employeeService = new EmployeeService();

    public UserRegisterDialog(Frame owner) {
        super(owner, "用户注册", true);
        initializeUI();
        loadEmployees();
        setupEventHandlers();
    }

    private void initializeUI() {
        setSize(450, 400);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("用户账号注册", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 表单
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("用户名*："), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("密码*："), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // 确认密码
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("确认密码*："), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        // 关联员工
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("关联员工："), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        employeeCombo = new JComboBox<>();
        employeeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Employee) {
                    Employee emp = (Employee) value;
                    setText(emp.getEmpName() + " (ID:" + emp.getEmpId() + ")");
                }
                return this;
            }
        });
        formPanel.add(employeeCombo, gbc);

        // 角色
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("角色*："), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        roleCombo = new JComboBox<>(new String[]{"user", "admin"});
        formPanel.add(roleCombo, gbc);

        // 提示标签
        JLabel tipLabel = new JLabel("带 * 的为必填项");
        tipLabel.setForeground(Color.RED);
        tipLabel.setFont(tipLabel.getFont().deriveFont(Font.ITALIC));

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(tipLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        registerButton = new JButton("注册");
        cancelButton = new JButton("取消");
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void loadEmployees() {
        employeeCombo.removeAllItems();

        // 添加"不关联"选项
        Employee noLink = new Employee();
        noLink.setEmpId(0);
        noLink.setEmpName("--不关联员工--");
        employeeCombo.addItem(noLink);

        // 加载所有员工
        List<Employee> employees = employeeService.getAllEmployees();
        for (Employee emp : employees) {
            employeeCombo.addItem(emp);
        }

        employeeCombo.setSelectedIndex(0);
    }

    private void setupEventHandlers() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void performRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // 获取关联员工
        Employee selectedEmployee = (Employee) employeeCombo.getSelectedItem();
        Integer empId = null;
        if (selectedEmployee != null && selectedEmployee.getEmpId() > 0) {
            empId = selectedEmployee.getEmpId();
        }

        String role = roleCombo.getSelectedItem().toString();

        // 调用服务层注册
        boolean success = userService.registerUser(username, password, confirmPassword, empId, role);

        if (success) {
            // 注册成功，清空表单
            usernameField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            employeeCombo.setSelectedIndex(0);
            roleCombo.setSelectedIndex(0);
        }
    }
}