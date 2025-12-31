package com.employee.dao;

import com.employee.model.Department;
import java.sql.SQLException;
import java.util.List;

public interface DepartmentDAO {
    boolean addDepartment(Department dept) throws SQLException;
    boolean updateDepartment(Department dept) throws SQLException;
    boolean deleteDepartment(int deptId) throws SQLException;
    Department getDepartmentById(int deptId) throws SQLException;
    List<Department> getAllDepartments() throws SQLException;
    List<Department> searchDepartments(String keyword) throws SQLException; // 添加这一行
}