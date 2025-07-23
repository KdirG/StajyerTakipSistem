package com.sts.stajyertakipsistem.dao;

import com.sts.stajyertakipsistem.model.Evrak;
import com.sts.stajyertakipsistem.db.DatabaseConnection;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvrakDAO {

    private static final Logger LOGGER = Logger.getLogger(EvrakDAO.class.getName());

    public int addEvrak(Evrak evrak) {
        String sql = "INSERT INTO EVRAK (EVRAK_ID, EVRAK_DOSYA_YOLU) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            int newId;
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT NEXT VALUE FOR GEN_EVRAK_ID FROM RDB$DATABASE")) {
                if (rs.next()) {
                    newId = rs.getInt(1);
                } else {
                    throw new SQLException("Evrak için yeni ID alınamadı.");
                }
            }
            
            evrak.setEvrakId(newId);

            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, evrak.getEvrakId());
                ps.setString(2, evrak.getDosyaYolu());
                ps.executeUpdate();
                return newId; 
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Evrak eklenirken hata.", e);
            return 0; 
        }
    }

    public boolean updateEvrak(Evrak evrak) {
        String sql = "UPDATE EVRAK SET EVRAK_DOSYA_YOLU = ? WHERE EVRAK_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, evrak.getDosyaYolu());
            ps.setInt(2, evrak.getEvrakId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Evrak güncellenirken hata. ID: " + evrak.getEvrakId(), e);
            return false;
        }
    }
    
    public Evrak getEvrakById(int evrakId) {
        String sql = "SELECT * FROM EVRAK WHERE EVRAK_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, evrakId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Evrak(
                        rs.getInt("EVRAK_ID"), 
                        rs.getString("EVRAK_DOSYA_YOLU")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Evrak getirilirken hata oluştu. ID: " + evrakId, e);
        }
        return null;
    }

    public boolean deleteEvrak(int evrakId) {
        String sql = "DELETE FROM EVRAK WHERE EVRAK_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, evrakId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Evrak silinirken hata oluştu. ID: " + evrakId, e);
            return false;
        }
    }
}