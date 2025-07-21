package com.sts.stajyertakipsistem.service;

import com.sts.stajyertakipsistem.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author kadir
 */
public class UserManager {

    public boolean authUser(String username, String password) {
        String sql = "SELECT SIFRE FROM KULLANICI WHERE KULLANICI_ADI = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("SIFRE");
                    // Burada parola karşılaştırması yapılacak.
                    // Güvenli bir uygulama için parolaların hash'lenerek saklanması ve
                    // hash'lenmiş değerlerin karşılaştırılması gerekir.
                    // Şimdilik basit bir eşleştirme yapıyoruz.
                    return password.equals(dbPassword);
                }
            }
        } catch (SQLException e) {
            System.err.println("Kullanıcı doğrulanırken veritabanı hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}