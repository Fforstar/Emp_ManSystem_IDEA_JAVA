package com.employee.model;

import java.sql.Time;
import java.util.Date;

public class Attendance {
    private int attId;
    private int empId;
    private Date attDate;
    private Time checkIn;
    private Time checkOut;
    private String status;

    // 显示用
    private String empName;
    private String deptName;
    private double workHours;

    public Attendance() {}

    public int getAttId() { return attId; }
    public void setAttId(int attId) { this.attId = attId; }
    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }
    public Date getAttDate() { return attDate; }
    public void setAttDate(Date attDate) { this.attDate = attDate; }
    public Time getCheckIn() { return checkIn; }
    public void setCheckIn(Time checkIn) { this.checkIn = checkIn; }
    public Time getCheckOut() { return checkOut; }
    public void setCheckOut(Time checkOut) { this.checkOut = checkOut; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public double getWorkHours() {
        if (checkIn != null && checkOut != null) {
            long diff = checkOut.getTime() - checkIn.getTime();
            return diff / (1000.0 * 60.0 * 60.0); // 转换为小时
        }
        return 0;
    }
}