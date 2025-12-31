package com.employee.model;

public class Department {
    private int deptId;
    private String deptName;
    private int deptManagerId;
    private int parentDeptId;
    private String createdDate;

    public Department() {}

    public Department(String deptName) {
        this.deptName = deptName;
    }

    // 关键：添加这个构造函数
    public Department(String deptName, int deptId) {
        this.deptName = deptName;
        this.deptId = deptId;
    }

    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public int getDeptManagerId() { return deptManagerId; }
    public void setDeptManagerId(int deptManagerId) { this.deptManagerId = deptManagerId; }

    public int getParentDeptId() { return parentDeptId; }
    public void setParentDeptId(int parentDeptId) { this.parentDeptId = parentDeptId; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    @Override
    public String toString() {
        return deptName;
    }
}