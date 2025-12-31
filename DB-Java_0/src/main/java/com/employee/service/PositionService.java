package com.employee.service;

import com.employee.dao.PositionDAO;
import com.employee.dao.PositionDAOImpl;
import com.employee.model.Position;
import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PositionService {
    private PositionDAO positionDAO = new PositionDAOImpl();

    public boolean addPosition(Position pos) {
        try {
            if (!validatePosition(pos)) return false;

            boolean success = positionDAO.addPosition(pos);
            if (success) {
                JOptionPane.showMessageDialog(null, "职位添加成功！");
            }
            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "添加失败: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePosition(Position pos) {
        try {
            if (!validatePosition(pos)) return false;

            boolean success = positionDAO.updatePosition(pos);
            if (success) {
                JOptionPane.showMessageDialog(null, "职位更新成功！");
            }
            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "更新失败: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePosition(int posId) {
        int confirm = JOptionPane.showConfirmDialog(null, "确定删除该职位吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return false;

        try {
            return positionDAO.deletePosition(posId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "删除失败: " + e.getMessage());
            return false;
        }
    }

    public List<Position> getAllPositions() {
        try {
            return positionDAO.getAllPositions();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "查询失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Position getPositionById(int posId) {
        try {
            return positionDAO.getPositionById(posId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Position> searchPositions(String keyword) {
        try {
            return positionDAO.searchPositions(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "搜索失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private boolean validatePosition(Position pos) {
        if (pos.getPosName() == null || pos.getPosName().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "职位名称不能为空！");
            return false;
        }

        if (pos.getBasicSalary() < 0) {
            JOptionPane.showMessageDialog(null, "基础薪资不能为负数！");
            return false;
        }

        return true;
    }
}