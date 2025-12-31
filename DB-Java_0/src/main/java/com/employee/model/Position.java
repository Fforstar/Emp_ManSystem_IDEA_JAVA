package com.employee.model;

public class Position {
    private int posId;
    private String posName;
    private int posLevel;
    private double basicSalary;

    public Position() {}

    public Position(String posName) {
        this.posName = posName;
    }

    // 关键：添加这个构造函数
    public Position(String posName, int posId) {
        this.posName = posName;
        this.posId = posId;
    }

    public Position(String posName, int posLevel, double basicSalary) {
        this.posName = posName;
        this.posLevel = posLevel;
        this.basicSalary = basicSalary;
    }

    public int getPosId() { return posId; }
    public void setPosId(int posId) { this.posId = posId; }

    public String getPosName() { return posName; }
    public void setPosName(String posName) { this.posName = posName; }

    public int getPosLevel() { return posLevel; }
    public void setPosLevel(int posLevel) { this.posLevel = posLevel; }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    @Override
    public String toString() {
        return posName;
    }
}