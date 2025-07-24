package com.sts.stajyertakipsistem.dao;

import com.sts.stajyertakipsistem.db.DatabaseConnection;
import com.sts.stajyertakipsistem.model.Izin;

import java.sql.*; // SQLException dahil tüm sql sınıfları için
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IzinDAO {

    private static final Logger LOGGER = Logger.getLogger(IzinDAO.class.getName());

    
    public IzinDAO() {
        // İleride bağlantı havuzu gibi bir yapı kullanılırsa burada başlatılabilir.
    }

   
    private Izin populateIzinFromResultSet(ResultSet rs) throws SQLException {
        Izin izin = new Izin();
        izin.setIzinId(rs.getInt("IZIN_ID"));
        izin.setStajyerId(rs.getInt("STAJYER_ID"));
        izin.setIzinSebep(rs.getString("IZIN_SEBEP"));

        Date baslangicSql = rs.getDate("IZIN_BASLANGIC");
        if (baslangicSql != null) {
            izin.setIzinBaslangic(baslangicSql.toLocalDate());
        }

        Date bitisSql = rs.getDate("IZIN_BITIS");
        if (bitisSql != null) {
            izin.setIzinBitis(bitisSql.toLocalDate());
        }
        return izin;
    }

   
    public void addIzin(Izin izin) throws SQLException { // Metot artık boolean yerine void dönüyor ve SQLException fırlatıyor
        String sql = "INSERT INTO IZIN (IZIN_ID, STAJYER_ID, IZIN_BASLANGIC, IZIN_BITIS, IZIN_SEBEP) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Firebird Generator kullanarak yeni ID al
            int newIzinId;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT NEXT VALUE FOR GEN_IZIN_ID FROM RDB$DATABASE")) {
                if (rs.next()) {
                    newIzinId = rs.getInt(1);
                } else {
                    // Generator'dan ID alınamazsa SQLException fırlat
                    throw new SQLException("Generator'dan yeni ID alınamadı.");
                }
            }
            izin.setIzinId(newIzinId); // Oluşturulan ID'yi Izin nesnesine ata

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, izin.getIzinId());
                ps.setInt(2, izin.getStajyerId());
                ps.setDate(3, izin.getIzinBaslangic() != null ? Date.valueOf(izin.getIzinBaslangic()) : null);
                ps.setDate(4, izin.getIzinBitis() != null ? Date.valueOf(izin.getIzinBitis()) : null);
                ps.setString(5, izin.getIzinSebep());

                ps.executeUpdate(); // Değer döndürmek yerine sadece komutu çalıştırıyoruz
                // Başarılı olursa loglama (opsiyonel)
                LOGGER.log(Level.INFO, "İzin başarıyla veritabanına eklendi. ID: {0}", newIzinId);
            }
        } catch (SQLException e) {
            // Hatayı logla ve yeniden fırlat, böylece çağrı yapan yakalayabilir.
            LOGGER.log(Level.SEVERE, "İzin eklenirken kritik bir veritabanı hatası oluştu: " + e.getMessage(), e);
            throw e; // SQLException'ı tekrar fırlatıyoruz
        }
    }

   
    public boolean updateIzin(Izin izin) throws SQLException { // Metot artık SQLException fırlatıyor
        String sql = "UPDATE IZIN SET STAJYER_ID = ?, IZIN_BASLANGIC = ?, IZIN_BITIS = ?, IZIN_SEBEP = ? WHERE IZIN_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, izin.getStajyerId());
            ps.setDate(2, izin.getIzinBaslangic() != null ? Date.valueOf(izin.getIzinBaslangic()) : null);
            ps.setDate(3, izin.getIzinBitis() != null ? Date.valueOf(izin.getIzinBitis()) : null);
            ps.setString(4, izin.getIzinSebep());
            ps.setInt(5, izin.getIzinId());
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                LOGGER.log(Level.INFO, "İzin başarıyla güncellendi. ID: {0}", izin.getIzinId());
            } else {
                LOGGER.log(Level.WARNING, "İzin ID {0} güncellenemedi, kayıt bulunamadı veya değişiklik yapılmadı.", izin.getIzinId());
            }
            return success;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "İzin ID " + izin.getIzinId() + " güncellenirken kritik bir veritabanı hatası oluştu: " + e.getMessage(), e);
            throw e; // SQLException'ı tekrar fırlatıyoruz
        }
    }

    
    public Izin getIzinById(int izinId) throws SQLException { // Metot artık SQLException fırlatıyor
        String sql = "SELECT * FROM IZIN WHERE IZIN_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, izinId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LOGGER.log(Level.INFO, "İzin ID {0} başarıyla bulundu.", izinId);
                    return populateIzinFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "İzin ID " + izinId + " getirilirken kritik bir veritabanı hatası oluştu: " + e.getMessage(), e);
            throw e; // SQLException'ı tekrar fırlatıyoruz
        }
        return null; // Kayıt bulunamazsa null döner
    }

    
    public List<Izin> getIzinlerByStajyerId(int stajyerId) throws SQLException { // Metot artık SQLException fırlatıyor
        List<Izin> izinler = new ArrayList<>();
        String sql = "SELECT * FROM IZIN WHERE STAJYER_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stajyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    izinler.add(populateIzinFromResultSet(rs));
                }
            }
            LOGGER.log(Level.INFO, "Stajyer ID {0} için {1} adet izin bulundu.", new Object[]{stajyerId, izinler.size()});
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Stajyer ID " + stajyerId + " için izinler getirilirken kritik bir veritabanı hatası oluştu: " + e.getMessage(), e);
            throw e; // SQLException'ı tekrar fırlatıyoruz
        }
        return izinler;
    }

    
    public boolean deleteIzin(int izinId) throws SQLException { // Metot artık SQLException fırlatıyor
        String sql = "DELETE FROM IZIN WHERE IZIN_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, izinId);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                LOGGER.log(Level.INFO, "İzin başarıyla silindi. ID: {0}", izinId);
            } else {
                LOGGER.log(Level.WARNING, "İzin ID {0} silinemedi, kayıt bulunamadı.", izinId);
            }
            return success;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "İzin ID " + izinId + " silinirken kritik bir veritabanı hatası oluştu: " + e.getMessage(), e);
            throw e; // SQLException'ı tekrar fırlatıyoruz
        }
    }
}