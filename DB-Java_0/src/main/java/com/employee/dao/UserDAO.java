package com.employee.dao;

import com.employee.model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    User login(String username, String password) throws SQLException;
    User getUserByUsername(String username) throws SQLException;
    boolean addUser(User user) throws SQLException;
    boolean updateUserPassword(int userId, String newPassword) throws SQLException;
    boolean checkUsernameExists(String username) throws SQLException;
    List<User> getAllUsers() throws SQLException;
}