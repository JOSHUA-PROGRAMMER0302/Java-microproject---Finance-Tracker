package com.financetracker;
import com.financetracker.db.DBUtil;
import com.financetracker.db.DBConfig;
import java.sql.Connection;

public class DBTest {
    public static void main(String[] args) {
        try (Connection conn = DBConfig.getConnection()) {
            System.out.println("✅ Connection successful!");
            System.out.println("Connected to: " + conn.getMetaData().getDatabaseProductName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
