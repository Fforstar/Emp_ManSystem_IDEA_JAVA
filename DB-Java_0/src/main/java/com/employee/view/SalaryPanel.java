package com.employee.view;

import com.employee.model.Employee;
import com.employee.model.Salary;
import com.employee.service.EmployeeService;
import com.employee.service.SalaryService;
import com.employee.service.AuthService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;  // 关键：使用 java.util.List

public class SalaryPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Employee> employeeCombo;
    private JTextField monthField;
    private JButton searchButton;
    private JButton generateButton;
    private JButton addButton;
    private JButton deleteButton;
    private JButton calculateButton;

    private SalaryService salaryService = new SalaryService();
    private EmployeeService employeeService = new EmployeeService();
    private SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");

    private String[] columns = {"ID", "员工", "部门", "月份", "基本工资", "奖金", "扣款", "实发工资"};

    public SalaryPanel() {
        setLayout(new BorderLayout());

        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createTitledBorder("薪资管理"));

        // 员工选择
        controlPanel.add(new JLabel("员工："));
        employeeCombo = new JComboBox<>();
        loadEmployees();
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
        controlPanel.add(employeeCombo);

        // 月份输入
        controlPanel.add(new JLabel("月份："));
        monthField = new JTextField(monthFormat.format(new java.util.Date()), 10);
        controlPanel.add(monthField);

        // 按钮
        searchButton = new JButton("查询");
        generateButton = new JButton("生成工资");
        addButton = new JButton("添加记录");
        deleteButton = new JButton("删除");
        calculateButton = new JButton("批量计算");

        // 权限控制
        if (AuthService.isAdmin()) {
            controlPanel.add(searchButton);
            controlPanel.add(generateButton);
            controlPanel.add(addButton);
            controlPanel.add(deleteButton);
            controlPanel.add(calculateButton);
        } else {
            controlPanel.add(searchButton);
            controlPanel.add(new JLabel("  （普通用户只能查询）"));
        }

        add(controlPanel, BorderLayout.NORTH);

        // 中部表格
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);

        // 金额列右对齐
        for (int i = 4; i <= 7; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setText(String.format("%.2f", (Double) value));
                    return this;
                }
            });
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

        // 事件监听
        setupEventHandlers();

        // 初始不加载数据
        clearTable();
    }

    private void setupEventHandlers() {
        if (AuthService.isAdmin()) {
            searchButton.addActionListener(e -> searchSalaries());
            generateButton.addActionListener(e -> generateSalary());
            addButton.addActionListener(e -> addSalary());
            deleteButton.addActionListener(e -> deleteSalary());
            calculateButton.addActionListener(e -> batchCalculate());
        } else {
            searchButton.addActionListener(e -> searchSalaries());
        }
    }

    private void loadEmployees() {
        employeeCombo.removeAllItems();

        // 添加"全部"选项
        Employee all = new Employee();
        all.setEmpId(0);
        all.setEmpName("--全部员工--");
        employeeCombo.addItem(all);

        // 加载所有员工
        List<Employee> employees = employeeService.getAllEmployees();
        for (Employee emp : employees) {
            employeeCombo.addItem(emp);
        }
    }

    private void searchSalaries() {
        Employee selectedEmployee = (Employee) employeeCombo.getSelectedItem();
        String monthStr = monthField.getText().trim();

        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "请先选择员工！");
            return;
        }

        List<Salary> salaries;

        try {
            if (selectedEmployee.getEmpId() == 0) { // 查询全部
                salaries = salaryService.getAllSalaries();
            } else { // 查询指定员工
                salaries = salaryService.getEmployeeSalaries(selectedEmployee.getEmpId());
            }

            // 如果指定了月份，则过滤
            if (!monthStr.isEmpty()) {
                salaries.removeIf(s -> !monthFormat.format(s.getSalaryMonth()).equals(monthStr));
            }

            updateTable(salaries);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "查询失败: " + e.getMessage());
        }
    }

    private void generateSalary() {
        Employee selectedEmployee = (Employee) employeeCombo.getSelectedItem();
        String monthStr = monthField.getText().trim();

        if (selectedEmployee == null || selectedEmployee.getEmpId() == 0) {
            JOptionPane.showMessageDialog(this, "请选择具体员工！");
            return;
        }

        if (monthStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入月份（格式：2024-01）！");
            return;
        }

        // 验证月份格式
        try {
            monthFormat.parse(monthStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "月份格式错误！应为：yyyy-MM");
            return;
        }

        boolean success = salaryService.generateMonthlySalary(selectedEmployee.getEmpId(), monthStr);
        if (success) {
            searchSalaries(); // 刷新显示
        }
    }

    private void addSalary() {
        SalaryDialog dialog = new SalaryDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "添加薪资记录", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Salary salary = dialog.getSalary();
            if (salaryService.addSalary(salary)) {
                searchSalaries();
            }
        }
    }

    private void deleteSalary() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的薪资记录！");
            return;
        }

        int salaryId = (int) tableModel.getValueAt(selectedRow, 0);
        if (salaryService.deleteSalary(salaryId)) {
            searchSalaries();
        }
    }

    private void batchCalculate() {
        String monthStr = monthField.getText().trim();
        if (monthStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先输入要计算的月份！");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要为所有员工生成" + monthStr + "的工资吗？\n这会覆盖已存在的记录！",
                "批量生成", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        // 获取所有员工
        List<Employee> employees = employeeService.getAllEmployees();
        int successCount = 0;
        int failCount = 0;

        for (Employee emp : employees) {
            try {
                boolean success = salaryService.generateMonthlySalary(emp.getEmpId(), monthStr);
                if (success) successCount++;
                else failCount++;
            } catch (Exception e) {
                failCount++;
            }
        }

        JOptionPane.showMessageDialog(this,
                "批量完成！\n成功: " + successCount + " 人\n失败: " + failCount + " 人");

        searchSalaries();
    }

    private void updateTable(List<Salary> salaries) {
        tableModel.setRowCount(0);
        for (Salary salary : salaries) {
            Object[] row = {
                    salary.getSalaryId(),
                    salary.getEmpName(),
                    salary.getDeptName() != null ? salary.getDeptName() : "",
                    monthFormat.format(salary.getSalaryMonth()),
                    salary.getBasicSalary(),
                    salary.getBonus(),
                    salary.getDeduction(),
                    salary.getNetSalary()
            };
            tableModel.addRow(row);
        }
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }
}