package com.employee.dao;

import com.employee.model.Attendance;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface AttendanceDAO {
    boolean checkIn(int empId) throws SQLException;
    boolean checkOut(int empId) throws SQLException;
    List<Attendance> getAttendancesByEmployee(int empId) throws SQLException;
    List<Attendance> getAttendancesByDate(Date date) throws SQLException;
    List<Attendance> getAllAttendances() throws SQLException;
    boolean addAttendance(Attendance attendance) throws SQLException;
    Attendance getAttendanceByEmployeeAndDate(int empId, Date date) throws SQLException;
}