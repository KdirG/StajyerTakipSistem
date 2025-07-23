package com.sts.stajyertakipsistem.dao;

import com.sts.stajyertakipsistem.model.Okul;
import com.sts.stajyertakipsistem.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OkulDAO {

    private static final Logger LOGGER = Logger.getLogger(OkulDAO.class.getName());

    public int addOkul(Okul okul) {
        String sql = "INSERT INTO OKUL (OKUL_ID, OKUL_ADI, OKUL_TURU) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            int newId;
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT NEXT VALUE FOR GEN_OKUL_ID FROM RDB$DATABASE")) {
                if (rs.next()) {
                    newId = rs.getInt(1);
                } else {
                    throw new SQLException("Okul için yeni ID alınamadı.");
                }
            }
            
            okul.setOkulId(newId);

            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, okul.getOkulId());
                ps.setString(2, okul.getOkulAdi());
                ps.setString(3, okul.getOkulTuru());
                ps.executeUpdate();
                return newId; 
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Okul eklenirken hata.", e);
            return 0; 
        }
    }

    public boolean updateOkul(Okul okul) {
        String sql = "UPDATE OKUL SET OKUL_ADI = ?, OKUL_TURU = ? WHERE OKUL_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, okul.getOkulAdi());
            ps.setString(2, okul.getOkulTuru());
            ps.setInt(3, okul.getOkulId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Okul güncellenirken hata. ID: " + okul.getOkulId(), e);
            return false;
        }
    }

    public Okul getOkulById(int okulId) {
        String sql = "SELECT * FROM OKUL WHERE OKUL_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, okulId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Okul(
                        rs.getInt("OKUL_ID"),
                        rs.getString("OKUL_ADI"),
                        rs.getString("OKUL_TURU")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Okul getirilirken hata. ID: " + okulId, e);
        }
        return null;
    }

    public List<Okul> getAllOkullar() {
        List<Okul> okullar = new ArrayList<>();
        String sql = "SELECT * FROM OKUL";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                okullar.add(new Okul(
                    rs.getInt("OKUL_ID"),
                    rs.getString("OKUL_ADI"),
                    rs.getString("OKUL_TURU")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Tüm okullar getirilirken hata.", e);
        }
        return okullar;
    }

    public boolean deleteOkul(int okulId) {
        String sql = "DELETE FROM OKUL WHERE OKUL_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, okulId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Okul silinirken hata. ID: " + okulId, e);
            return false;
        }
    }
}