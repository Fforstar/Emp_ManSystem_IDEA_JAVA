package com.employee.dao;

import com.employee.model.Employee;
import java.sql.SQLException;
import java.util.List;

public interface EmployeeDAO {
    boolean addEmployee(Employee employee) throws SQLException;
    boolean updateEmployee(Employee employee) throws SQLException;
    boolean deleteEmployee(int empId) throws SQLException;
    Employee getEmployeeById(int empId) throws SQLException;
    List<Employee> getAllEmployees() throws SQLException;
    List<Employee> searchEmployees(String keyword) throws SQLException;
}