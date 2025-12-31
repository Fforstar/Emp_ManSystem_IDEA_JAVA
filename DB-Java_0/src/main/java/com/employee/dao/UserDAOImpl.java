package com.employee.dao;

import com.employee.model.User;
import com.employee.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT u.*, e.emp_name FROM sys_user u " +
                "LEFT JOIN employee e ON u.emp_id = e.emp_id " +
                "WHERE u.username=? AND u.password=? AND u.is_active=TRUE";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    @Override
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT u.*, e.emp_name FROM sys_user u " +
                "LEFT JOIN employee e ON u.emp_id = e.emp_id " +
                "WHERE u.username=?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    @Override
    public boolean addUser(User user) throws SQLException {
        String sql = "INSERT INTO sys_user (username, password, emp_id, role, is_active) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setObject(3, user.getEmpId());
            pstmt.setString(4, user.getRole());
            pstmt.setBoolean(5, user.isActive());

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public boolean updateUserPassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE sys_user SET password=? WHERE user_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public boolean checkUsernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM sys_user WHERE username=?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT u.*, e.emp_name FROM sys_user u " +
                "LEFT JOIN employee e ON u.emp_id = e.emp_id " +
                "ORDER BY u.user_id DESC";

        List<User> users = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            return users;
        } finally {
            DBUtil.closeAll(conn, stmt, rs);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));

        int empId = rs.getInt("emp_id");
        if (!rs.wasNull()) {
            user.setEmpId(empId);
        }

        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("is_active"));
        user.setEmpName(rs.getString("emp_name"));
        return user;
    }
}