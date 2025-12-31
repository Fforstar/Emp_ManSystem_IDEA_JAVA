package com.employee.service;

import com.employee.dao.EmployeeDAO;
import com.employee.dao.EmployeeDAOImpl;
import com.employee.model.Employee;
import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class EmployeeService {
    private EmployeeDAO employeeDAO = new EmployeeDAOImpl();

    public boolean addEmployee(Employee employee) {
        try {
            if (!validateEmployee(employee)) return false;
            boolean result = employeeDAO.addEmployee(employee);
            if (result) {
                JOptionPane.showMessageDialog(null, "添加成功！");
            }
            return result;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "操作失败: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEmployee(Employee employee) {
        try {
            if (!validateEmployee(employee)) return false;
            boolean result = employeeDAO.updateEmployee(employee);
            if (result) {
                JOptionPane.showMessageDialog(null, "更新成功！");
            }
            return result;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "操作失败: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteEmployee(int empId) {
        int confirm = JOptionPane.showConfirmDialog(null, "确定删除吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return false;

        try {
            return employeeDAO.deleteEmployee(empId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "删除失败: " + e.getMessage());
            return false;
        }
    }

    public List<Employee> getAllEmployees() {
        try {
            return employeeDAO.getAllEmployees();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "查询失败: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public List<Employee> searchEmployees(String keyword) {
        try {
            return employeeDAO.searchEmployees(keyword);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "搜索失败: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public Employee getEmployeeById(int empId) {
        try {
            return employeeDAO.getEmployeeById(empId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "查询失败: " + e.getMessage());
            return null;
        }
    }

    private boolean validateEmployee(Employee employee) {
        if (employee.getEmpName() == null || employee.getEmpName().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "姓名不能为空！");
            return false;
        }
        if (!"男".equals(employee.getGender()) && !"女".equals(employee.getGender())) {
            JOptionPane.showMessageDialog(null, "性别必须是'男'或'女'！");
            return false;
        }
        if (employee.getHireDate() == null) {
            JOptionPane.showMessageDialog(null, "入职日期不能为空！");
            return false;
        }
        return true;
    }
}