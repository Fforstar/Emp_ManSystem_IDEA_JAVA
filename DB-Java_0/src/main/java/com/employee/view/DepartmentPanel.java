package com.employee.view;

import com.employee.model.Department;
import com.employee.service.DepartmentService;
import com.employee.service.AuthService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;  // 关键：使用 java.util.List

public class DepartmentPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    private DepartmentService departmentService = new DepartmentService();

    private String[] columns = {"ID", "部门名称"};

    public DepartmentPanel() {
        setLayout(new BorderLayout());

        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createTitledBorder("部门管理"));

        searchField = new JTextField(15);
        searchButton = new JButton("搜索");
        addButton = new JButton("添加");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        refreshButton = new JButton("刷新");

        // 权限控制
        if (AuthService.isAdmin()) {
            controlPanel.add(new JLabel("搜索："));
            controlPanel.add(searchField);
            controlPanel.add(searchButton);
            controlPanel.add(addButton);
            controlPanel.add(editButton);
            controlPanel.add(deleteButton);
            controlPanel.add(refreshButton);
        } else {
            controlPanel.add(new JLabel("  （普通用户只读）"));
        }

        add(controlPanel, BorderLayout.NORTH);

        // 中部表格
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // 事件监听
        setupEventHandlers();

        // 初始加载数据
        loadDepartments();
    }

    private void setupEventHandlers() {
        if (AuthService.isAdmin()) {
            searchButton.addActionListener(e -> searchDepartments());
            searchField.addActionListener(e -> searchDepartments());
            addButton.addActionListener(e -> addDepartment());
            editButton.addActionListener(e -> editDepartment());
            deleteButton.addActionListener(e -> deleteDepartment());
            refreshButton.addActionListener(e -> loadDepartments());
        }
    }

    private void loadDepartments() {
        tableModel.setRowCount(0);
        List<Department> departments = departmentService.getAllDepartments();

        for (Department dept : departments) {
            Object[] row = {dept.getDeptId(), dept.getDeptName()};
            tableModel.addRow(row);
        }
    }

    private void searchDepartments() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadDepartments();
            return;
        }

        tableModel.setRowCount(0);
        List<Department> departments = departmentService.searchDepartments(keyword);

        for (Department dept : departments) {
            Object[] row = {dept.getDeptId(), dept.getDeptName()};
            tableModel.addRow(row);
        }
    }

    private void addDepartment() {
        DepartmentDialog dialog = new DepartmentDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "添加部门", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Department newDept = dialog.getDepartment();
            if (departmentService.addDepartment(newDept)) {
                loadDepartments();
            }
        }
    }

    private void editDepartment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的部门！");
            return;
        }

        int deptId = (int) tableModel.getValueAt(selectedRow, 0);
        String deptName = (String) tableModel.getValueAt(selectedRow, 1);

        Department dept = new Department(deptName);
        dept.setDeptId(deptId);

        DepartmentDialog dialog = new DepartmentDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "编辑部门", dept);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Department updatedDept = dialog.getDepartment();
            updatedDept.setDeptId(deptId);
            if (departmentService.updateDepartment(updatedDept)) {
                loadDepartments();
            }
        }
    }

    private void deleteDepartment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的部门！");
            return;
        }

        String deptName = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除部门 " + deptName + " 吗？\n注意：部门下有员工时无法删除！",
                "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int deptId = (int) tableModel.getValueAt(selectedRow, 0);
            if (departmentService.deleteDepartment(deptId)) {
                loadDepartments();
            }
        }
    }
}