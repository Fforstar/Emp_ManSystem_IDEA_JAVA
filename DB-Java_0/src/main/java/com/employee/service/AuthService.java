package com.employee.service;

import com.employee.dao.UserDAO;
import com.employee.dao.UserDAOImpl;
import com.employee.model.User;
import javax.swing.*;
import java.sql.SQLException;

public class AuthService {
    private UserDAO userDAO = new UserDAOImpl();
    private static User currentUser;

    public boolean login(String username, String password) {
        try {
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "用户名和密码不能为空！");
                return false;
            }

            User user = userDAO.login(username, password);
            if (user != null) {
                currentUser = user;
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "用户名或密码错误！");
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "登录失败: " + e.getMessage());
            return false;
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }

    // 关键方法：判断是否为管理员
    public static boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }
}