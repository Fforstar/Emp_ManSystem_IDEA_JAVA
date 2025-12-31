package com.employee.model;

import java.util.Date;

public class Employee {
    private int empId;
    private String empName;
    private String gender;
    private Date birthDate;
    private Date hireDate;
    private String phone;
    private String email;
    private String address;
    private int deptId;
    private int posId;
    private String status;

    // 用于显示
    private String deptName;
    private String posName;

    public Employee() {}

    public Employee(String empName, String gender, Date hireDate) {
        this.empName = empName;
        this.gender = gender;
        this.hireDate = hireDate;
    }

    // Getters and Setters
    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }
    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    public Date getHireDate() { return hireDate; }
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }
    public int getPosId() { return posId; }
    public void setPosId(int posId) { this.posId = posId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getPosName() { return posName; }
    public void setPosName(String posName) { this.posName = posName; }
}