package com.employee.service;

import com.employee.dao.DepartmentDAO;
import com.employee.dao.DepartmentDAOImpl;
import com.employee.model.Department;
import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentService {
    private DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    public boolean addDepartment(Department dept) {
        try {
            if (dept.getDeptName() == null || dept.getDeptName().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "部门名称不能为空！", "验证错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            boolean result = departmentDAO.addDepartment(dept);
            if (result) {
                JOptionPane.showMessageDialog(null, "部门添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
            return result;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "添加失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean updateDepartment(Department dept) {
        try {
            if (dept.getDeptName() == null || dept.getDeptName().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "部门名称不能为空！", "验证错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            boolean result = departmentDAO.updateDepartment(dept);
            if (result) {
                JOptionPane.showMessageDialog(null, "部门更新成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
            return result;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "更新失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean deleteDepartment(int deptId) {
        try {
            int confirm = JOptionPane.showConfirmDialog(null, "确定要删除该部门吗？", "确认删除", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return false;
            }

            boolean result = departmentDAO.deleteDepartment(deptId);
            if (result) {
                JOptionPane.showMessageDialog(null, "部门删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
            return result;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "删除失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public List<Department> getAllDepartments() {
        try {
            return departmentDAO.getAllDepartments();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "查询失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }

    public Department getDepartmentById(int deptId) {
        try {
            return departmentDAO.getDepartmentById(deptId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Department> searchDepartments(String keyword) {
        try {
            return departmentDAO.searchDepartments(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "搜索失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }
}