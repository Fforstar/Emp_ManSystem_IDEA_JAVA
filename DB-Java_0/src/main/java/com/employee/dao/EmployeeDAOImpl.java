package com.employee.dao;

import com.employee.model.Employee;
import com.employee.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements EmployeeDAO {

    @Override
    public boolean addEmployee(Employee employee) throws SQLException {
        String sql = "INSERT INTO employee (emp_name, gender, hire_date, dept_id, pos_id, status) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, employee.getEmpName());
            pstmt.setString(2, employee.getGender());
            pstmt.setDate(3, new java.sql.Date(employee.getHireDate().getTime()));
            pstmt.setInt(4, employee.getDeptId());
            pstmt.setInt(5, employee.getPosId());
            pstmt.setString(6, employee.getStatus());

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public boolean updateEmployee(Employee employee) throws SQLException {
        String sql = "UPDATE employee SET emp_name=?, gender=?, hire_date=?, dept_id=?, pos_id=?, status=? WHERE emp_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, employee.getEmpName());
            pstmt.setString(2, employee.getGender());
            pstmt.setDate(3, new java.sql.Date(employee.getHireDate().getTime()));
            pstmt.setInt(4, employee.getDeptId());
            pstmt.setInt(5, employee.getPosId());
            pstmt.setString(6, employee.getStatus());
            pstmt.setInt(7, employee.getEmpId());

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public boolean deleteEmployee(int empId) throws SQLException {
        String sql = "DELETE FROM employee WHERE emp_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empId);

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public Employee getEmployeeById(int empId) throws SQLException {
        String sql = "SELECT e.*, d.dept_name, p.pos_name FROM employee e " +
                "LEFT JOIN department d ON e.dept_id = d.dept_id " +
                "LEFT JOIN position p ON e.pos_id = p.pos_id WHERE e.emp_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToEmployee(rs);
            }
            return null;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    @Override
    public List<Employee> getAllEmployees() throws SQLException {
        String sql = "SELECT e.*, d.dept_name, p.pos_name FROM employee e " +
                "LEFT JOIN department d ON e.dept_id = d.dept_id " +
                "LEFT JOIN position p ON e.pos_id = p.pos_id ORDER BY e.emp_id DESC";

        List<Employee> employees = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
            return employees;
        } finally {
            DBUtil.closeAll(conn, stmt, rs);
        }
    }

    @Override
    public List<Employee> searchEmployees(String keyword) throws SQLException {
        String sql = "SELECT e.*, d.dept_name, p.pos_name FROM employee e " +
                "LEFT JOIN department d ON e.dept_id = d.dept_id " +
                "LEFT JOIN position p ON e.pos_id = p.pos_id " +
                "WHERE e.emp_name LIKE ? OR d.dept_name LIKE ? OR p.pos_name LIKE ? " +
                "ORDER BY e.emp_id DESC";

        List<Employee> employees = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
            return employees;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmpId(rs.getInt("emp_id"));
        employee.setEmpName(rs.getString("emp_name"));
        employee.setGender(rs.getString("gender"));
        employee.setHireDate(rs.getDate("hire_date"));
        employee.setDeptId(rs.getInt("dept_id"));
        employee.setPosId(rs.getInt("pos_id"));
        employee.setStatus(rs.getString("status"));
        employee.setDeptName(rs.getString("dept_name"));
        employee.setPosName(rs.getString("pos_name"));
        return employee;
    }
}