package com.employee.dao;

import com.employee.model.Salary;
import com.employee.util.DBUtil;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalaryDAOImpl implements SalaryDAO {

    @Override
    public boolean addSalary(Salary salary) throws SQLException {
        String sql = "INSERT INTO salary (emp_id, salary_month, basic_salary, bonus, deduction, net_salary) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, salary.getEmpId());
            pstmt.setDate(2, new java.sql.Date(salary.getSalaryMonth().getTime()));
            pstmt.setDouble(3, salary.getBasicSalary());
            pstmt.setDouble(4, salary.getBonus());
            pstmt.setDouble(5, salary.getDeduction());
            pstmt.setDouble(6, salary.getNetSalary());

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public boolean updateSalary(Salary salary) throws SQLException {
        String sql = "UPDATE salary SET basic_salary=?, bonus=?, deduction=?, net_salary=? WHERE salary_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, salary.getBasicSalary());
            pstmt.setDouble(2, salary.getBonus());
            pstmt.setDouble(3, salary.getDeduction());
            pstmt.setDouble(4, salary.getNetSalary());
            pstmt.setInt(5, salary.getSalaryId());

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public boolean deleteSalary(int salaryId) throws SQLException {
        String sql = "DELETE FROM salary WHERE salary_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, salaryId);

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public Salary getSalaryById(int salaryId) throws SQLException {
        String sql = "SELECT s.*, e.emp_name FROM salary s " +
                "JOIN employee e ON s.emp_id = e.emp_id WHERE s.salary_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, salaryId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSalary(rs);
            }
            return null;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    @Override
    public List<Salary> getSalariesByEmployee(int empId) throws SQLException {
        String sql = "SELECT s.*, e.emp_name, d.dept_name FROM salary s " +
                "JOIN employee e ON s.emp_id = e.emp_id " +
                "LEFT JOIN department d ON e.dept_id = d.dept_id " +
                "WHERE s.emp_id=? ORDER BY s.salary_month DESC";

        List<Salary> salaries = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                salaries.add(mapResultSetToSalary(rs));
            }
            return salaries;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    @Override
    public List<Salary> getAllSalaries() throws SQLException {
        String sql = "SELECT s.*, e.emp_name, d.dept_name FROM salary s " +
                "JOIN employee e ON s.emp_id = e.emp_id " +
                "LEFT JOIN department d ON e.dept_id = d.dept_id " +
                "ORDER BY s.salary_month DESC, e.emp_id";

        List<Salary> salaries = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                salaries.add(mapResultSetToSalary(rs));
            }
            return salaries;
        } finally {
            DBUtil.closeAll(conn, stmt, rs);
        }
    }

    @Override
    public List<Salary> getSalariesByMonth(Date month) throws SQLException {
        String sql = "SELECT s.*, e.emp_name, d.dept_name FROM salary s " +
                "JOIN employee e ON s.emp_id = e.emp_id " +
                "LEFT JOIN department d ON e.dept_id = d.dept_id " +
                "WHERE DATE_FORMAT(s.salary_month, '%Y-%m') = DATE_FORMAT(?, '%Y-%m') " +
                "ORDER BY e.emp_id";

        List<Salary> salaries = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, new java.sql.Date(month.getTime()));

            rs = pstmt.executeQuery();
            while (rs.next()) {
                salaries.add(mapResultSetToSalary(rs));
            }
            return salaries;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    @Override
    public boolean calculateAndSaveSalary(int empId, Date month) throws SQLException {
        String sql = "SELECT e.emp_id, p.basic_salary FROM employee e " +
                "JOIN position p ON e.pos_id = p.pos_id WHERE e.emp_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                double basicSalary = rs.getDouble("basic_salary");

                Salary salary = new Salary();
                salary.setEmpId(empId);
                salary.setSalaryMonth(month);
                salary.setBasicSalary(basicSalary);
                salary.setBonus(0);
                salary.setDeduction(0);
                salary.calculateNetSalary();

                return addSalary(salary);
            }
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    private Salary mapResultSetToSalary(ResultSet rs) throws SQLException {
        Salary salary = new Salary();
        salary.setSalaryId(rs.getInt("salary_id"));
        salary.setEmpId(rs.getInt("emp_id"));
        salary.setSalaryMonth(rs.getDate("salary_month"));
        salary.setBasicSalary(rs.getDouble("basic_salary"));
        salary.setBonus(rs.getDouble("bonus"));
        salary.setDeduction(rs.getDouble("deduction"));
        salary.setNetSalary(rs.getDouble("net_salary"));
        salary.setEmpName(rs.getString("emp_name"));
        salary.setDeptName(rs.getString("dept_name"));
        return salary;
    }
}