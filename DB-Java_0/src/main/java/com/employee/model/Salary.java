package com.employee.model;

import java.util.Date;

public class Salary {
    private int salaryId;
    private int empId;
    private Date salaryMonth;
    private double basicSalary;
    private double bonus;
    private double deduction;
    private double netSalary;

    // 显示用
    private String empName;
    private String deptName;

    public Salary() {}

    public Salary(int empId, Date salaryMonth, double basicSalary, double bonus, double deduction) {
        this.empId = empId;
        this.salaryMonth = salaryMonth;
        this.basicSalary = basicSalary;
        this.bonus = bonus;
        this.deduction = deduction;
        calculateNetSalary();
    }

    public void calculateNetSalary() {
        this.netSalary = this.basicSalary + this.bonus - this.deduction;
    }

    public int getSalaryId() { return salaryId; }
    public void setSalaryId(int salaryId) { this.salaryId = salaryId; }
    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }
    public Date getSalaryMonth() { return salaryMonth; }
    public void setSalaryMonth(Date salaryMonth) { this.salaryMonth = salaryMonth; }
    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { this.bonus = bonus; }
    public double getDeduction() { return deduction; }
    public void setDeduction(double deduction) { this.deduction = deduction; }
    public double getNetSalary() { return netSalary; }
    public void setNetSalary(double netSalary) { this.netSalary = netSalary; }
    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
}