package com.employee.dao;

import com.employee.model.Position;
import com.employee.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PositionDAOImpl implements PositionDAO {

    @Override
    public boolean addPosition(Position pos) throws SQLException {
        String sql = "INSERT INTO position (pos_name, pos_level, basic_salary) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, pos.getPosName());
            pstmt.setInt(2, pos.getPosLevel());
            pstmt.setDouble(3, pos.getBasicSalary());

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public boolean updatePosition(Position pos) throws SQLException {
        String sql = "UPDATE position SET pos_name=?, pos_level=?, basic_salary=? WHERE pos_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, pos.getPosName());
            pstmt.setInt(2, pos.getPosLevel());
            pstmt.setDouble(3, pos.getBasicSalary());
            pstmt.setInt(4, pos.getPosId());

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public boolean deletePosition(int posId) throws SQLException {
        String sql = "DELETE FROM position WHERE pos_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, posId);

            return pstmt.executeUpdate() > 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    @Override
    public Position getPositionById(int posId) throws SQLException {
        String sql = "SELECT * FROM position WHERE pos_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, posId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPosition(rs);
            }
            return null;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    @Override
    public List<Position> getAllPositions() throws SQLException {
        String sql = "SELECT * FROM position ORDER BY pos_level, pos_id";

        List<Position> positions = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                positions.add(mapResultSetToPosition(rs));
            }
            return positions;
        } finally {
            DBUtil.closeAll(conn, stmt, rs);
        }
    }

    @Override
    public List<Position> searchPositions(String keyword) throws SQLException {
        String sql = "SELECT * FROM position WHERE pos_name LIKE ? ORDER BY pos_level, pos_id";

        List<Position> positions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");

            rs = pstmt.executeQuery();
            while (rs.next()) {
                positions.add(mapResultSetToPosition(rs));
            }
            return positions;
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
    }

    private Position mapResultSetToPosition(ResultSet rs) throws SQLException {
        Position pos = new Position();
        pos.setPosId(rs.getInt("pos_id"));
        pos.setPosName(rs.getString("pos_name"));
        pos.setPosLevel(rs.getInt("pos_level"));
        pos.setBasicSalary(rs.getDouble("basic_salary"));
        return pos;
    }
}