package com.employee.service;

import com.employee.dao.AttendanceDAO;
import com.employee.dao.AttendanceDAOImpl;
import com.employee.model.Attendance;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class AttendanceService {
    private AttendanceDAO attendanceDAO = new AttendanceDAOImpl();

    /**
     * 员工签到
     */
    public boolean checkIn(int empId) {
        try {
            if (empId <= 0) {
                JOptionPane.showMessageDialog(null, "无效的员工ID！");
                return false;
            }

            boolean success = attendanceDAO.checkIn(empId);
            if (success) {
                JOptionPane.showMessageDialog(null, "签到成功！");
            }
            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "签到失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 员工签退
     */
    public boolean checkOut(int empId) {
        try {
            if (empId <= 0) {
                JOptionPane.showMessageDialog(null, "无效的员工ID！");
                return false;
            }

            boolean success = attendanceDAO.checkOut(empId);
            if (success) {
                JOptionPane.showMessageDialog(null, "签退成功！");
            }
            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "签退失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取员工考勤记录
     */
    public List<Attendance> getEmployeeAttendances(int empId) {
        try {
            return attendanceDAO.getAttendancesByEmployee(empId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "查询失败: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 获取所有考勤记录
     */
    public List<Attendance> getAllAttendances() {
        try {
            return attendanceDAO.getAllAttendances();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "查询失败: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 手动添加考勤记录（管理员）
     */
    public boolean addAttendance(Attendance attendance) {
        try {
            boolean success = attendanceDAO.addAttendance(attendance);
            if (success) {
                JOptionPane.showMessageDialog(null, "考勤记录添加成功！");
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "添加失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取今日考勤统计
     */
    public String getTodayAttendanceStats() {
        try {
            List<Attendance> todayAttendances = attendanceDAO.getAttendancesByDate(new Date());
            int total = todayAttendances.size();
            int present = (int) todayAttendances.stream().filter(a -> "出勤".equals(a.getStatus())).count();
            int absent = total - present;

            return String.format("今日出勤: %d人 | 缺勤: %d人 | 总计: %d人", present, absent, total);
        } catch (SQLException e) {
            return "统计失败: " + e.getMessage();
        }
    }
}