package com.employee.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import javax.swing.JOptionPane;

public class DBUtil {
    private static String url;
    private static String username;
    private static String password;

    static {
        try {
            // 读取配置文件
            Properties props = new Properties();
            InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");

            if (is == null) {
                // 如果文件不存在，使用默认值
                url = "jdbc:mysql://localhost:3306/employee_management?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
                username = "root";
                password = "123456";  // 修改为你的密码
            } else {
                props.load(is);
                url = props.getProperty("db.url");
                username = props.getProperty("db.username");
                password = props.getProperty("db.password");
                is.close();
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("数据库配置加载失败！");
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库连接失败: " + e.getMessage());
            return null;
        }
    }

    public static void closeAll(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}