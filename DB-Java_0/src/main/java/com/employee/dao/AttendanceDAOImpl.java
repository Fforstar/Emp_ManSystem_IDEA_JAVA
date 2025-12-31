package com.employee.dao;

import com.employee.model.Attendance;
import com.employee.util.DBUtil;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AttendanceDAOImpl implements AttendanceDAO {

    @Override
    public boolean checkIn(int empId) throws SQLException {
        String checkSql = "SELECT att_id FROM attendance WHERE emp_id=? AND att_date=CURDATE()";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, empId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "今日已签到，不能重复签到！");
                return false;
            }

            String insertSql = "INSERT INTO attendance (emp_id, att_date, check_in, status) VALUES (?, CURDATE(), NOW(), '出勤')";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, empId);

            int result = insertStmt.executeUpdate();
            insertStmt.close();

            return result > 0;

        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    @Override
    public boolean checkOut(int empId) throws SQLException {
        String sql = "UPDATE attendance SET check_out=NOW(), status='出勤' " +
                "WHERE emp_id=? AND att_date=CURDATE() AND check_out IS NULL";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empId);

            int result = pstmt.executeUpdate();
            if (result == 0) {
                JOptionPane.showMessageDialog(null, "今日未签到或已签退！");
                return false;
            }
            return true;

        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public List<Attendance> getAttendancesByEmployee(int empId) throws SQLException {
        String sql = "SELECT a.*, e.emp_name, d.dept_name FROM attendance a " +
                "JOIN employee e ON a.emp_id = e.emp_id " +
                "LEFT JOIN department d ON e.dept_id = d.dept_id " +
                "WHERE a.emp_id=? ORDER BY a.att_date DESC";

        List<Attendance> attendances = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                attendances.add(mapResultSetToAttendance(rs));
            }
            return attendances;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        Attendance att = new Attendance();
        att.setAttId(rs.getInt("att_id"));
        att.setEmpId(rs.getInt("emp_id"));
        att.setAttDate(rs.getDate("att_date"));
        att.setCheckIn(rs.getTime("check_in"));
        att.setCheckOut(rs.getTime("check_out"));
        att.setStatus(rs.getString("status"));
        att.setEmpName(rs.getString("emp_name"));
        att.setDeptName(rs.getString("dept_name"));
        return att;
    }

    @Override
    public List<Attendance> getAllAttendances() throws SQLException {
        String sql = "SELECT a.*, e.emp_name, d.dept_name FROM attendance a " +
                "JOIN employee e ON a.emp_id = e.emp_id " +
                "LEFT JOIN department d ON e.dept_id = d.dept_id " +
                "ORDER BY a.att_date DESC, e.emp_id";

        List<Attendance> attendances = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                attendances.add(mapResultSetToAttendance(rs));
            }
            return attendances;
        } finally {
            DBUtil.closeAll(conn, stmt, rs);
        }
    }

    @Override
    public boolean addAttendance(Attendance attendance) throws SQLException {
        String sql = "INSERT INTO attendance (emp_id, att_date, check_in, check_out, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, attendance.getEmpId());
            pstmt.setDate(2, new java.sql.Date(attendance.getAttDate().getTime()));
            pstmt.setTime(3, attendance.getCheckIn());
            pstmt.setTime(4, attendance.getCheckOut());
            pstmt.setString(5, attendance.getStatus());

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public Attendance getAttendanceByEmployeeAndDate(int empId, Date date) throws SQLException {
        String sql = "SELECT * FROM attendance WHERE emp_id=? AND att_date=?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empId);
            pstmt.setDate(2, new java.sql.Date(date.getTime()));

            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAttendance(rs);
            }
            return null;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    @Override
    public List<Attendance> getAttendancesByDate(Date date) throws SQLException {
        String sql = "SELECT a.*, e.emp_name, d.dept_name FROM attendance a " +
                "JOIN employee e ON a.emp_id = e.emp_id " +
                "LEFT JOIN department d ON e.dept_id = d.dept_id " +
                "WHERE a.att_date=? ORDER BY a.check_in DESC";

        List<Attendance> attendances = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, new java.sql.Date(date.getTime()));

            rs = pstmt.executeQuery();
            while (rs.next()) {
                attendances.add(mapResultSetToAttendance(rs));
            }
            return attendances;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }
}