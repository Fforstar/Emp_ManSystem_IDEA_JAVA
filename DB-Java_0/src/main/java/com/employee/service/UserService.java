package com.employee.service;

import com.employee.dao.UserDAO;
import com.employee.dao.UserDAOImpl;
import com.employee.model.User;
import javax.swing.*;
import java.sql.SQLException;

public class UserService {
    private UserDAO userDAO = new UserDAOImpl();

    /**
     * 用户注册
     */
    public boolean registerUser(String username, String password, String confirmPassword,
                                Integer empId, String role) {
        try {
            // 验证输入
            if (!validateRegistration(username, password, confirmPassword)) {
                return false;
            }

            // 检查用户名是否存在
            if (userDAO.checkUsernameExists(username)) {
                JOptionPane.showMessageDialog(null, "用户名已存在！", "注册失败", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // 创建用户对象
            User newUser = new User(username, password, role);
            newUser.setEmpId(empId);
            newUser.setActive(true);

            // 添加到数据库
            boolean success = userDAO.addUser(newUser);
            if (success) {
                JOptionPane.showMessageDialog(null, "用户注册成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "注册失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * 修改密码
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword, String confirmPassword) {
        try {
            if (!validatePasswordChange(oldPassword, newPassword, confirmPassword)) {
                return false;
            }

            boolean success = userDAO.updateUserPassword(userId, newPassword);
            if (success) {
                JOptionPane.showMessageDialog(null, "密码修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "修改失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private boolean validateRegistration(String username, String password, String confirmPassword) {
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "用户名不能为空！", "验证错误", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (username.length() < 3 || username.length() > 20) {
            JOptionPane.showMessageDialog(null, "用户名长度必须在3-20之间！", "验证错误", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (password == null || password.length() < 6) {
            JOptionPane.showMessageDialog(null, "密码长度至少6位！", "验证错误", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null, "两次输入的密码不一致！", "验证错误", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean validatePasswordChange(String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword == null || oldPassword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "旧密码不能为空！", "验证错误", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (newPassword == null || newPassword.length() < 6) {
            JOptionPane.showMessageDialog(null, "新密码长度至少6位！", "验证错误", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null, "两次输入的新密码不一致！", "验证错误", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (oldPassword.equals(newPassword)) {
            JOptionPane.showMessageDialog(null, "新密码不能与旧密码相同！", "验证错误", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }
}