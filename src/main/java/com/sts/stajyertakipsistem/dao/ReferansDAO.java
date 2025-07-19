package com.sts.stajyertakipsistem.dao;

import com.sts.stajyertakipsistem.model.Referans;
import com.sts.stajyertakipsistem.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; 
public class ReferansDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public boolean addReferans(Referans referans) {
        String sql = "INSERT INTO REFERANSLAR (REFERANS_ID, AD_SOYAD, TELEFON_NO, KURUM) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Eğer referansId boşsa yeni bir UUID oluştur
            if (referans.getReferansId() == null || referans.getReferansId().isEmpty()) {
                referans.setReferansId(UUID.randomUUID().toString());
            }

            ps.setString(1, referans.getReferansId());
            ps.setString(2, referans.getAdSoyad());
            ps.setString(3, referans.getTelefonNo());
            ps.setString(4, referans.getKurum());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateReferans(Referans referans) {
        if (referans.getReferansId() == null || referans.getReferansId().isEmpty()) {
            System.err.println("Referans ID geçerli değil, güncelleme yapılamadı.");
            return false;
        }

        String sql = "UPDATE REFERANSLAR SET AD_SOYAD = ?, TELEFON_NO = ?, KURUM = ? WHERE REFERANS_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, referans.getAdSoyad());
            ps.setString(2, referans.getTelefonNo());
            ps.setString(3, referans.getKurum());
            ps.setString(4, referans.getReferansId()); // ID'ye göre güncelle

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Referans getReferansById(String referansId) {
        Referans referans = null;
        String sql = "SELECT REFERANS_ID, AD_SOYAD, TELEFON_NO, KURUM FROM REFERANSLAR WHERE REFERANS_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, referansId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    referans = new Referans(
                        rs.getString("REFERANS_ID"),
                        rs.getString("AD_SOYAD"),
                        rs.getString("TELEFON_NO"),
                        rs.getString("KURUM")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return referans;
    }

    public List<Referans> getAllReferanslar() {
        List<Referans> referanslar = new ArrayList<>();
        String sql = "SELECT REFERANS_ID, AD_SOYAD, TELEFON_NO, KURUM FROM REFERANSLAR";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                referanslar.add(new Referans(
                    rs.getString("REFERANS_ID"),
                    rs.getString("AD_SOYAD"),
                    rs.getString("TELEFON_NO"),
                    rs.getString("KURUM")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return referanslar;
    }

    public boolean deleteReferans(String referansId) {
        String sql = "DELETE FROM REFERANSLAR WHERE REFERANS_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, referansId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Arama için ek metot (opsiyonel)
    public List<Referans> searchReferanslar(String searchText) {
        List<Referans> referanslar = new ArrayList<>();
        String sql = "SELECT REFERANS_ID, AD_SOYAD, TELEFON_NO, KURUM FROM REFERANSLAR WHERE AD_SOYAD LIKE ? OR TELEFON_NO LIKE ? OR KURUM LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + searchText + "%");
            ps.setString(2, "%" + searchText + "%");
            ps.setString(3, "%" + searchText + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    referanslar.add(new Referans(
                        rs.getString("REFERANS_ID"),
                        rs.getString("AD_SOYAD"),
                        rs.getString("TELEFON_NO"),
                        rs.getString("KURUM")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return referanslar;
    }
}