package com.employee.view;

import com.employee.model.Attendance;
import com.employee.model.Employee;
import com.employee.service.AttendanceService;
import com.employee.service.EmployeeService;
import com.employee.service.AuthService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;  // 确认是java.util.List

public class AttendancePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Employee> employeeCombo;
    private JButton checkInButton;
    private JButton checkOutButton;
    private JButton searchButton;
    private JButton addButton;
    private JButton refreshButton;
    private JLabel statsLabel;

    private AttendanceService attendanceService = new AttendanceService();
    private EmployeeService employeeService = new EmployeeService();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    private String[] columns = {"ID", "员工", "部门", "日期", "签到时间", "签退时间", "工作时长", "状态"};

    public AttendancePanel() {
        setLayout(new BorderLayout());

        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createTitledBorder("考勤管理"));

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

        // 按钮
        checkInButton = new JButton("签到");
        checkOutButton = new JButton("签退");
        searchButton = new JButton("查询记录");
        addButton = new JButton("补录");
        refreshButton = new JButton("刷新");

        // 权限控制
        if (AuthService.isAdmin()) {
            controlPanel.add(checkInButton);
            controlPanel.add(checkOutButton);
            controlPanel.add(searchButton);
            controlPanel.add(addButton);
            controlPanel.add(refreshButton);
        } else {
            controlPanel.add(checkInButton);
            controlPanel.add(checkOutButton);
            controlPanel.add(searchButton);
        }

        add(controlPanel, BorderLayout.NORTH);

        // 统计信息
        statsLabel = new JLabel("  📊 " + attendanceService.getTodayAttendanceStats());
        statsLabel.setFont(statsLabel.getFont().deriveFont(Font.BOLD));
        statsLabel.setForeground(new Color(41, 128, 185));

        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(BorderFactory.createEtchedBorder());
        statsPanel.add(statsLabel, BorderLayout.WEST);

        // 中部表格
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("考勤记录"));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 事件监听
        setupEventHandlers();

        // 初始不加载数据
        clearTable();
    }

    private void setupEventHandlers() {
        checkInButton.addActionListener(e -> checkIn());
        checkOutButton.addActionListener(e -> checkOut());
        searchButton.addActionListener(e -> searchAttendances());

        if (AuthService.isAdmin()) {
            addButton.addActionListener(e -> addAttendance());
            refreshButton.addActionListener(e -> loadAllAttendances());
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

    private void checkIn() {
        Employee selectedEmployee = (Employee) employeeCombo.getSelectedItem();
        if (selectedEmployee == null || selectedEmployee.getEmpId() == 0) {
            JOptionPane.showMessageDialog(this, "请选择要签到的员工！");
            return;
        }

        attendanceService.checkIn(selectedEmployee.getEmpId());
        statsLabel.setText("  📊 " + attendanceService.getTodayAttendanceStats());
    }

    private void checkOut() {
        Employee selectedEmployee = (Employee) employeeCombo.getSelectedItem();
        if (selectedEmployee == null || selectedEmployee.getEmpId() == 0) {
            JOptionPane.showMessageDialog(this, "请选择要签退的员工！");
            return;
        }

        attendanceService.checkOut(selectedEmployee.getEmpId());
        statsLabel.setText("  📊 " + attendanceService.getTodayAttendanceStats());
    }

    private void searchAttendances() {
        Employee selectedEmployee = (Employee) employeeCombo.getSelectedItem();
        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "请选择员工！");
            return;
        }

        List<Attendance> attendances;
        if (selectedEmployee.getEmpId() == 0) {
            attendances = attendanceService.getAllAttendances();
        } else {
            attendances = attendanceService.getEmployeeAttendances(selectedEmployee.getEmpId());
        }

        updateTable(attendances);
    }

    private void addAttendance() {
        AttendanceDialog dialog = new AttendanceDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "补录考勤", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            if (attendanceService.addAttendance(dialog.getAttendance())) {
                searchAttendances();
            }
        }
    }

    private void loadAllAttendances() {
        List<Attendance> attendances = attendanceService.getAllAttendances();
        updateTable(attendances);
    }

    private void updateTable(List<Attendance> attendances) {
        tableModel.setRowCount(0);
        for (Attendance att : attendances) {
            String workHoursStr = String.format("%.2f小时", att.getWorkHours());

            Object[] row = {
                    att.getAttId(),
                    att.getEmpName(),
                    att.getDeptName() != null ? att.getDeptName() : "",
                    dateFormat.format(att.getAttDate()),
                    att.getCheckIn() != null ? timeFormat.format(att.getCheckIn()) : "",
                    att.getCheckOut() != null ? timeFormat.format(att.getCheckOut()) : "",
                    workHoursStr,
                    att.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }
}