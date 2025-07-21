package com.sts.stajyertakipsistem.dao;

import com.sts.stajyertakipsistem.model.Referans;
import com.sts.stajyertakipsistem.db.DatabaseConnection;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReferansDAO {
    private static final Logger LOGGER = Logger.getLogger(ReferansDAO.class.getName());

    public int addReferans(Referans referans) {
        String sql = "INSERT INTO REFERANS (REFERANS_ID, AD_SOYAD, TELEFON_NO, KURUM) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            int newId;
            // 1. Veritabanından yeni ID al
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT NEXT VALUE FOR GEN_REFERANS_ID FROM RDB$DATABASE")) {
                if (rs.next()) {
                    newId = rs.getInt(1);
                } else {
                    throw new SQLException("Referans için yeni ID alınamadı.");
                }
            }
            // 2. Alınan ID'yi nesneye ata
            referans.setReferansId(newId);

            // 3. Kayıt işlemini yap
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, referans.getReferansId());
                ps.setString(2, referans.getAdSoyad());
                ps.setString(3, referans.getTelefonNo());
                ps.setString(4, referans.getKurum());
                ps.executeUpdate();
                return newId; // Başarılı olursa yeni ID'yi döndür
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Referans eklenirken hata.", e);
            return 0; // Hata durumunda 0 döndür
        }
    }

    public boolean updateReferans(Referans referans) {
        String sql = "UPDATE REFERANS SET AD_SOYAD = ?, TELEFON_NO = ?, KURUM = ? WHERE REFERANS_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, referans.getAdSoyad());
            ps.setString(2, referans.getTelefonNo());
            ps.setString(3, referans.getKurum());
            ps.setInt(4, referans.getReferansId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Referans güncellenirken hata. ID: " + referans.getReferansId(), e);
            return false;
        }
    }

    public Referans getReferansById(int referansId) {
        String sql = "SELECT * FROM REFERANS WHERE REFERANS_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, referansId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Referans(
                        rs.getInt("REFERANS_ID"),
                        rs.getString("AD_SOYAD"),
                        rs.getString("TELEFON_NO"),
                        rs.getString("KURUM")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Referans getirilirken hata. ID: " + referansId, e);
        }
        return null;
    }

    public boolean deleteReferans(int referansId) {
        String sql = "DELETE FROM REFERANS WHERE REFERANS_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, referansId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Referans silinirken hata. ID: " + referansId, e);
            return false;
        }
    }
}