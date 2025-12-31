package com.employee.view;

import com.employee.service.AuthService;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private UserRegisterDialog registerDialog;

    public MainFrame() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeUI();
        setupTabbedPane();
        setupStatusBar();
        setupEventHandlers();
    }

    private void initializeUI() {
        setTitle("公司员工管理系统");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setContentPane(mainPanel);

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("公司员工管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton = createStyledButton("退出登录", new Color(231, 76, 60), Color.WHITE);
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }

    private void setupTabbedPane() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("员工管理", new EmployeePanel());
        tabbedPane.addTab("部门管理", new DepartmentPanel());
        tabbedPane.addTab("职位管理", new PositionPanel());
        tabbedPane.addTab("薪资管理", new SalaryPanel());
        tabbedPane.addTab("考勤管理", new AttendancePanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(236, 240, 241));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setBackground(new Color(236, 240, 241));

        String username = AuthService.getCurrentUser() != null ?
                AuthService.getCurrentUser().getUsername() : "未知";
        String role = AuthService.isAdmin() ? "管理员" : "普通用户";

        JLabel userLabel = new JLabel("用户：" + username);
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        userLabel.setForeground(new Color(52, 73, 94));

        JLabel roleLabel = new JLabel("角色：" + role);
        roleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        roleLabel.setForeground(AuthService.isAdmin() ? new Color(231, 76, 60) : new Color(46, 204, 113));

        infoPanel.add(userLabel);
        infoPanel.add(new JSeparator(SwingConstants.VERTICAL));
        infoPanel.add(roleLabel);

        statusPanel.add(infoPanel, BorderLayout.WEST);

        if (AuthService.isAdmin()) {
            JButton registerButton = createStyledButton("用户注册", new Color(46, 204, 113), Color.WHITE);
            registerButton.addActionListener(e -> showRegisterDialog());
            statusPanel.add(registerButton, BorderLayout.EAST);
        }

        add(statusPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void setupEventHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MainFrame.this,
                        "确定要退出系统吗？", "确认退出",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    AuthService.logout();  // 修复：小写l
                    System.exit(0);
                }
            }
        });
    }

    private void showRegisterDialog() {
        if (registerDialog == null) {
            registerDialog = new UserRegisterDialog(this);
        }
        registerDialog.setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "确定要退出登录吗？", "确认退出登录",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            AuthService.logout();  // 修复：小写l
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }
}