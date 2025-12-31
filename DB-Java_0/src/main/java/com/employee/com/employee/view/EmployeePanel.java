package com.employee.view;

import com.employee.model.Department;
import com.employee.model.Employee;
import com.employee.model.Position;
import com.employee.service.DepartmentService;
import com.employee.service.EmployeeService;
import com.employee.service.PositionService;
import com.employee.service.AuthService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class EmployeePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton loadAllButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JLabel statusLabel;

    private EmployeeService employeeService = new EmployeeService();
    private DepartmentService departmentService = new DepartmentService();
    private PositionService positionService = new PositionService();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String[] columns = {"ID", "姓名", "性别", "入职日期", "部门", "职位", "状态"};

    public EmployeePanel() {
        setLayout(new BorderLayout());

        // 顶部搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("数据查询"));

        searchField = new JTextField(20);
        searchButton = new JButton("搜索");
        loadAllButton = new JButton("加载全部");

        // 回车和按钮触发搜索
        searchField.addActionListener(e -> searchEmployees());
        searchButton.addActionListener(e -> searchEmployees());
        loadAllButton.addActionListener(e -> loadAllEmployees());

        searchPanel.add(new JLabel("关键字："));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(loadAllButton);

        // 权限提示
        if (!AuthService.isAdmin()) {
            searchPanel.add(new JLabel("  （普通用户只能搜索，无法查看全部）"));
        }

        add(searchPanel, BorderLayout.NORTH);

        // 中部表格（初始为空）
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("查询结果"));
        add(scrollPane, BorderLayout.CENTER);

        // 底部状态标签和按钮
        statusLabel = new JLabel("  💡 提示：请输入搜索关键字或点击'加载全部'查看数据");
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.ITALIC));

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.add(statusLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("添加");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");

        // 只有管理员才能增删改
        if (AuthService.isAdmin()) {
            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
        } else {
            buttonPanel.add(new JLabel("  （普通用户无编辑权限）"));
        }

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(statusPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // 事件监听
        setupEventHandlers();

        // 初始不加载数据
        clearTable();
    }

    private void setupEventHandlers() {
        // 双击编辑（管理员）
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && AuthService.isAdmin()) {
                    editEmployee();
                }
            }
        });

        // 按钮事件
        if (AuthService.isAdmin()) {
            addButton.addActionListener(e -> addEmployee());
            editButton.addActionListener(e -> editEmployee());
            deleteButton.addActionListener(e -> deleteEmployee());
        }
    }

    private void searchEmployees() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入搜索关键字！", "提示", JOptionPane.WARNING_MESSAGE);
            searchField.requestFocus();
            return;
        }

        statusLabel.setText("  🔍 正在搜索: " + keyword);
        statusLabel.setForeground(Color.BLUE);

        List<Employee> employees = employeeService.searchEmployees(keyword);

        if (employees.isEmpty()) {
            statusLabel.setText("  ❌ 未找到匹配的数据");
            statusLabel.setForeground(Color.RED);
            clearTable();
        } else {
            statusLabel.setText("  ✅ 找到 " + employees.size() + " 条记录");
            statusLabel.setForeground(new Color(0, 150, 0));
            updateTable(employees);
        }
    }

    private void loadAllEmployees() {
        if (!AuthService.isAdmin()) {
            JOptionPane.showMessageDialog(this, "普通用户无权限查看全部数据！", "权限不足", JOptionPane.WARNING_MESSAGE);
            searchField.requestFocus();
            return;
        }

        statusLabel.setText("  📊 正在加载全部数据...");
        statusLabel.setForeground(Color.BLUE);

        List<Employee> employees = employeeService.getAllEmployees();

        if (employees.isEmpty()) {
            statusLabel.setText("  ⚠️ 暂无数据");
            statusLabel.setForeground(Color.ORANGE);
        } else {
            statusLabel.setText("  📋 已加载 " + employees.size() + " 条记录");
            statusLabel.setForeground(new Color(0, 150, 0));
        }

        updateTable(employees);
    }

    private void updateTable(List<Employee> employees) {
        tableModel.setRowCount(0);
        for (Employee emp : employees) {
            Object[] row = {
                    emp.getEmpId(),
                    emp.getEmpName(),
                    emp.getGender(),
                    dateFormat.format(emp.getHireDate()),
                    emp.getDeptName() != null ? emp.getDeptName() : "",
                    emp.getPosName() != null ? emp.getPosName() : "",
                    emp.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }

    private void addEmployee() {
        EmployeeDialog dialog = new EmployeeDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "添加员工", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            if (employeeService.addEmployee(dialog.getEmployee())) {
                if (statusLabel.getText().contains("已加载")) {
                    loadAllEmployees();
                } else {
                    clearTable();
                    statusLabel.setText("  ✅ 添加成功，请重新搜索查看");
                    statusLabel.setForeground(new Color(0, 150, 0));
                }
            }
        }
    }

    private void editEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的员工！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int empId = (int) tableModel.getValueAt(selectedRow, 0);
        Employee employee = employeeService.getEmployeeById(empId);

        if (employee != null) {
            EmployeeDialog dialog = new EmployeeDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "编辑员工", employee);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                if (employeeService.updateEmployee(dialog.getEmployee())) {
                    if (statusLabel.getText().contains("已加载")) {
                        loadAllEmployees();
                    } else if (statusLabel.getText().contains("找到")) {
                        searchEmployees();
                    }
                }
            }
        }
    }

    private void deleteEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的员工！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String empName = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除员工 " + empName + " 吗？", "确认删除",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int empId = (int) tableModel.getValueAt(selectedRow, 0);
            if (employeeService.deleteEmployee(empId)) {
                if (statusLabel.getText().contains("已加载")) {
                    loadAllEmployees();
                } else if (statusLabel.getText().contains("找到")) {
                    searchEmployees();
                }
            }
        }
    }
}