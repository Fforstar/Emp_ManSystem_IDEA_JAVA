package com.employee.dao;

import com.employee.model.Position;
import java.sql.SQLException;
import java.util.List;

public interface PositionDAO {
    boolean addPosition(Position pos) throws SQLException;
    boolean updatePosition(Position pos) throws SQLException;
    boolean deletePosition(int posId) throws SQLException;
    Position getPositionById(int posId) throws SQLException;
    List<Position> getAllPositions() throws SQLException;
    List<Position> searchPositions(String keyword) throws SQLException;
}