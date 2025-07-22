package com.sts.stajyertakipsistem.service;

import com.sts.stajyertakipsistem.db.DatabaseConnection; // Veritabanı bağlantısı için
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane; // Hata/bilgi mesajları için

public class UserManager {

    private static final Logger LOGGER = Logger.getLogger(UserManager.class.getName());

    /**
     * Kullanıcı adı ve şifre ile kullanıcıyı doğrular.
     * Şifreler veritabanında düz metin olarak saklandığı varsayılır.
     *
     * @param username Doğrulanacak kullanıcının adı.
     * @param password Doğrulanacak şifre.
     * @return Kullanıcı adı ve şifre eşleşiyorsa true, aksi takdirde false.
     */
    public boolean authUser(String username, String password) {
        String sql = "SELECT SIFRE FROM KULLANICI WHERE KULLANICI_ADI = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("SIFRE");
                    // Parolalar düz metin olarak karşılaştırılıyor
                    return password.equals(dbPassword);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Kullanıcı doğrulanırken veritabanı hatası", e);
            // Geliştirme ortamında ek bilgi için
            JOptionPane.showMessageDialog(null, "Veritabanı bağlantı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /**
     * Belirtilen kullanıcının şifresini değiştirir.
     * Eski şifreyi doğrular ve yeni şifreyi veritabanında düz metin olarak günceller.
     *
     * @param username Şifresi değiştirilecek kullanıcının adı.
     * @param oldPassword Kullanıcının mevcut şifresi.
     * @param newPassword Kullanıcının yeni şifresi.
     * @return Şifre değiştirme işlemi başarılıysa true, aksi takdirde false.
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        // 1. Önce kullanıcının eski şifresini doğrula
        // authUser metodu zaten gerekli kontrolü ve hata mesajını içeriyor.
        if (!authUser(username, oldPassword)) {
            // authUser içinde mesaj gösterildiği için burada tekrar göstermiyoruz
            return false;
        }

        // 2. Eski şifre doğruysa, yeni şifreyi veritabanında güncelle
        String sql = "UPDATE KULLANICI SET SIFRE = ? WHERE KULLANICI_ADI = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword); // Yeni şifre doğrudan veritabanına kaydediliyor
            pstmt.setString(2, username);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Eğer 1 veya daha fazla satır etkilendiyse başarılı demektir.

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Şifre değiştirilirken veritabanı hatası", e);
            JOptionPane.showMessageDialog(null, "Şifre değiştirilirken bir hata oluştu: " + e.getMessage(), "Veritabanı Hatası", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}