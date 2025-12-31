package com.employee.service;

import com.employee.dao.SalaryDAO;
import com.employee.dao.SalaryDAOImpl;
import com.employee.model.Salary;
import javax.swing.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SalaryService {
    private SalaryDAO salaryDAO = new SalaryDAOImpl();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

    public boolean addSalary(Salary salary) {
        try {
            // 验证数据
            if (salary.getEmpId() <= 0) {
                JOptionPane.showMessageDialog(null, "请选择员工！");
                return false;
            }

            if (salary.getSalaryMonth() == null) {
                JOptionPane.showMessageDialog(null, "请选择薪资月份！");
                return false;
            }

            if (salary.getBasicSalary() < 0) {
                JOptionPane.showMessageDialog(null, "基本工资不能为负数！");
                return false;
            }

            // 自动计算实发工资
            salary.calculateNetSalary();

            boolean success = salaryDAO.addSalary(salary);
            if (success) {
                JOptionPane.showMessageDialog(null, "薪资记录添加成功！");
            }
            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "操作失败: " + e.getMessage());
            return false;
        }
    }

    public boolean generateMonthlySalary(int empId, String monthStr) {
        try {
            Date month = dateFormat.parse(monthStr);

            // 检查是否已存在该月工资
            List<Salary> existing = salaryDAO.getSalariesByEmployee(empId);
            for (Salary s : existing) {
                if (dateFormat.format(s.getSalaryMonth()).equals(monthStr)) {
                    JOptionPane.showMessageDialog(null, "该员工" + monthStr + "的工资已存在！");
                    return false;
                }
            }

            return salaryDAO.calculateAndSaveSalary(empId, month);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "生成失败: " + e.getMessage());
            return false;
        }
    }

    public List<Salary> getEmployeeSalaries(int empId) {
        try {
            return salaryDAO.getSalariesByEmployee(empId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "查询失败: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public List<Salary> getAllSalaries() {
        try {
            return salaryDAO.getAllSalaries();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "查询失败: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public boolean deleteSalary(int salaryId) {
        int confirm = JOptionPane.showConfirmDialog(null, "确定删除该薪资记录吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return false;

        try {
            return salaryDAO.deleteSalary(salaryId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "删除失败: " + e.getMessage());
            return false;
        }
    }

    public Salary getSalaryById(int salaryId) {
        try {
            return salaryDAO.getSalaryById(salaryId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "查询失败: " + e.getMessage());
            return null;
        }
    }
}