package com.sts.stajyertakipsistem.dao;

import com.sts.stajyertakipsistem.model.Okul;
import com.sts.stajyertakipsistem.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OkulDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public int addOkul(Okul okul) {
        String sql = "INSERT INTO OKULLAR (OKUL_ADI, OKUL_TURU) VALUES (?, ?)";
        int generatedId = -1;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, okul.getOkulAdi());
            ps.setString(2, okul.getOkulTuru());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                        okul.setOkulId(generatedId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    public boolean updateOkul(Okul okul) {
        if (okul.getOkulId() <= 0) {
            System.err.println("Okul ID geçerli değil, güncelleme yapılamadı.");
            return false;
        }

        String sql = "UPDATE OKULLAR SET OKUL_ADI = ?, OKUL_TURU = ? WHERE OKUL_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, okul.getOkulAdi());
            ps.setString(2, okul.getOkulTuru());
            ps.setInt(3, okul.getOkulId());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Okul getOkulById(int okulId) {
        Okul okul = null;
        String sql = "SELECT OKUL_ID, OKUL_ADI, OKUL_TURU FROM OKULLAR WHERE OKUL_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, okulId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    okul = new Okul(
                        rs.getInt("OKUL_ID"),
                        rs.getString("OKUL_ADI"),
                        rs.getString("OKUL_TURU")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return okul;
    }

    public List<Okul> getAllOkullar() {
        List<Okul> okullar = new ArrayList<>();
        String sql = "SELECT OKUL_ID, OKUL_ADI, OKUL_TURU FROM OKULLAR";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                okullar.add(new Okul(
                    rs.getInt("OKUL_ID"),
                    rs.getString("OKUL_ADI"),
                    rs.getString("OKUL_TURU")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return okullar;
    }

    public boolean deleteOkul(int okulId) {
        String sql = "DELETE FROM OKULLAR WHERE OKUL_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, okulId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}