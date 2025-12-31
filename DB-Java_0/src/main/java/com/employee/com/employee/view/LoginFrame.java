package com.employee.view;

import com.employee.service.AuthService;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton exitButton;
    private AuthService authService;

    public LoginFrame() {
        authService = new AuthService();
        initializeUI();
        setupEventHandlers();
    }

    private void initializeUI() {
        setTitle("公司员工管理系统 - 登录");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        // 标题
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("公司员工管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("用户名："), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("密码："), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        loginButton = new JButton("登录");
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        loginButton.setBackground(new Color(41, 128, 185));
        loginButton.setForeground(Color.WHITE);

        registerButton = new JButton("注册新账号");
        registerButton.setPreferredSize(new Dimension(120, 40));
        registerButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.WHITE);

        exitButton = new JButton("退出");
        exitButton.setPreferredSize(new Dimension(120, 40));
        exitButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        exitButton.setBackground(new Color(231, 76, 60));
        exitButton.setForeground(Color.WHITE);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(exitButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void setupEventHandlers() {
        loginButton.addActionListener(e -> performLogin());
        registerButton.addActionListener(e -> showRegisterDialog());
        exitButton.addActionListener(e -> System.exit(0));

        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        usernameField.addKeyListener(enterKeyAdapter);
        passwordField.addKeyListener(enterKeyAdapter);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名！", "提示", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入密码！", "提示", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        if (authService.login(username, password)) {
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                LoginFrame.this.dispose();
            });
        } else {
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    private void showRegisterDialog() {
        UserRegisterDialog dialog = new UserRegisterDialog(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}