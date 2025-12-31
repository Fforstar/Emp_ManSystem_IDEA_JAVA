package com.employee.dao;

import com.employee.model.Salary;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface SalaryDAO {
    boolean addSalary(Salary salary) throws SQLException;
    boolean updateSalary(Salary salary) throws SQLException;
    boolean deleteSalary(int salaryId) throws SQLException;
    Salary getSalaryById(int salaryId) throws SQLException;
    List<Salary> getSalariesByEmployee(int empId) throws SQLException;
    List<Salary> getSalariesByMonth(Date month) throws SQLException;
    List<Salary> getAllSalaries() throws SQLException;
    boolean calculateAndSaveSalary(int empId, Date month) throws SQLException;
}