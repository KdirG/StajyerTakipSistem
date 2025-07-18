package com.sts.stajyertakipsistem.service;

import com.sts.stajyertakipsistem.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class UserManager {

    public boolean authUser(String username, String password) {
        String query = "SELECT COUNT(*) FROM KULLANICI WHERE KULLANICI_ADI = ? AND SIFRE = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
            return false;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Veritabanı bağlantı hatası veya sorgu hatası: " + e.getMessage(), "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
}