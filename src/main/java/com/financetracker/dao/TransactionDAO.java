package com.financetracker.dao;

import com.financetracker.db.DBUtil;
import com.financetracker.model.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public Transaction save(Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions(t_date, description, category, amount, t_type) VALUES (?,?,?,?,?) RETURNING id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // use java.sql.Date explicitly to avoid ambiguity
            ps.setDate(1, java.sql.Date.valueOf(t.getDate()));
            ps.setString(2, t.getDescription());
            ps.setString(3, t.getCategory());
            ps.setBigDecimal(4, t.getAmount());
            ps.setString(5, t.getType());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                t.setId(rs.getInt("id"));
            }
        }
        return t;
    }

    public List<Transaction> findAll() throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT id, t_date, description, category, amount, t_type FROM transactions ORDER BY t_date DESC, id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Transaction t = new Transaction();
                t.setId(rs.getInt("id"));
                // explicitly convert SQL Date to LocalDate
                t.setDate(rs.getDate("t_date").toLocalDate());
                t.setDescription(rs.getString("description"));
                t.setCategory(rs.getString("category"));
                t.setAmount(rs.getBigDecimal("amount"));
                t.setType(rs.getString("t_type"));
                list.add(t);
            }
        }
        return list;
    }

    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public BigDecimal getTotalByType(String type) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM transactions WHERE t_type = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                } else {
                    return BigDecimal.ZERO;
                }
            }
        }
    }
}
