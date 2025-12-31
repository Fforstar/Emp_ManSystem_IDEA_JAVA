package com.employee.view;

import com.employee.model.Position;
import com.employee.service.PositionService;
import com.employee.service.AuthService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;  // 关键：使用 java.util.List

public class PositionPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    private PositionService positionService = new PositionService();

    private String[] columns = {"ID", "职位名称", "职级", "基础薪资"};

    public PositionPanel() {
        setLayout(new BorderLayout());

        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createTitledBorder("职位管理"));

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

        // 金额列右对齐
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
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

        add(new JScrollPane(table), BorderLayout.CENTER);

        // 事件监听
        setupEventHandlers();

        // 初始加载数据
        loadPositions();
    }

    private void setupEventHandlers() {
        if (AuthService.isAdmin()) {
            searchButton.addActionListener(e -> searchPositions());
            searchField.addActionListener(e -> searchPositions());
            addButton.addActionListener(e -> addPosition());
            editButton.addActionListener(e -> editPosition());
            deleteButton.addActionListener(e -> deletePosition());
            refreshButton.addActionListener(e -> loadPositions());
        }
    }

    private void loadPositions() {
        tableModel.setRowCount(0);
        List<Position> positions = positionService.getAllPositions();

        for (Position pos : positions) {
            Object[] row = {
                    pos.getPosId(),
                    pos.getPosName(),
                    pos.getPosLevel(),
                    pos.getBasicSalary()
            };
            tableModel.addRow(row);
        }
    }

    private void searchPositions() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadPositions();
            return;
        }

        tableModel.setRowCount(0);
        List<Position> positions = positionService.searchPositions(keyword);

        for (Position pos : positions) {
            Object[] row = {
                    pos.getPosId(),
                    pos.getPosName(),
                    pos.getPosLevel(),
                    pos.getBasicSalary()
            };
            tableModel.addRow(row);
        }
    }

    private void addPosition() {
        PositionDialog dialog = new PositionDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "添加职位", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Position newPos = dialog.getPosition();
            if (positionService.addPosition(newPos)) {
                loadPositions();
            }
        }
    }

    private void editPosition() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的职位！");
            return;
        }

        int posId = (int) tableModel.getValueAt(selectedRow, 0);
        String posName = (String) tableModel.getValueAt(selectedRow, 1);
        int posLevel = (int) tableModel.getValueAt(selectedRow, 2);
        double basicSalary = (double) tableModel.getValueAt(selectedRow, 3);

        Position pos = new Position();
        pos.setPosId(posId);
        pos.setPosName(posName);
        pos.setPosLevel(posLevel);
        pos.setBasicSalary(basicSalary);

        PositionDialog dialog = new PositionDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "编辑职位", pos);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Position updatedPos = dialog.getPosition();
            updatedPos.setPosId(posId);
            if (positionService.updatePosition(updatedPos)) {
                loadPositions();
            }
        }
    }

    private void deletePosition() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的职位！");
            return;
        }

        String posName = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除职位 " + posName + " 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int posId = (int) tableModel.getValueAt(selectedRow, 0);
            if (positionService.deletePosition(posId)) {
                loadPositions();
            }
        }
    }
}