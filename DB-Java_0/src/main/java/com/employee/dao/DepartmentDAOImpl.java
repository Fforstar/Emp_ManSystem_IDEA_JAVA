package com.employee.dao;

import com.employee.model.Department;
import com.employee.util.DBUtil;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAOImpl implements DepartmentDAO {

    @Override
    public boolean addDepartment(Department dept) throws SQLException {
        String sql = "INSERT INTO department (dept_name, dept_manager_id, parent_dept_id, created_date) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, dept.getDeptName());
            pstmt.setObject(2, dept.getDeptManagerId() > 0 ? dept.getDeptManagerId() : null);
            pstmt.setObject(3, dept.getParentDeptId() > 0 ? dept.getParentDeptId() : null);
            pstmt.setString(4, dept.getCreatedDate());

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public boolean updateDepartment(Department dept) throws SQLException {
        String sql = "UPDATE department SET dept_name=?, dept_manager_id=?, parent_dept_id=? WHERE dept_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, dept.getDeptName());
            pstmt.setObject(2, dept.getDeptManagerId() > 0 ? dept.getDeptManagerId() : null);
            pstmt.setObject(3, dept.getParentDeptId() > 0 ? dept.getParentDeptId() : null);
            pstmt.setInt(4, dept.getDeptId());

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public boolean deleteDepartment(int deptId) throws SQLException {
        String sql = "DELETE FROM department WHERE dept_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, deptId);

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public Department getDepartmentById(int deptId) throws SQLException {
        String sql = "SELECT * FROM department WHERE dept_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, deptId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDepartment(rs);
            }
            return null;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    @Override
    public List<Department> getAllDepartments() throws SQLException {
        String sql = "SELECT * FROM department ORDER BY dept_id";

        List<Department> departments = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
            return departments;
        } finally {
            DBUtil.closeAll(conn, stmt, rs);
        }
    }

    @Override
    public List<Department> searchDepartments(String keyword) throws SQLException {
        String sql = "SELECT * FROM department WHERE dept_name LIKE ? ORDER BY dept_id";

        List<Department> departments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");

            rs = pstmt.executeQuery();
            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
            return departments;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    private Department mapResultSetToDepartment(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setDeptId(rs.getInt("dept_id"));
        dept.setDeptName(rs.getString("dept_name"));
        dept.setDeptManagerId(rs.getInt("dept_manager_id"));
        dept.setParentDeptId(rs.getInt("parent_dept_id"));
        dept.setCreatedDate(rs.getString("created_date"));
        return dept;
    }
}