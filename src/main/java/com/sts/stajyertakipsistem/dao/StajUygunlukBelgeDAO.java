package com.sts.stajyertakipsistem.dao;

import com.sts.stajyertakipsistem.db.DatabaseConnection;
import com.sts.stajyertakipsistem.model.StajUygunlukBelge;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StajUygunlukBelgeDAO {

    private static final Logger LOGGER = Logger.getLogger(StajUygunlukBelgeDAO.class.getName());

    public StajUygunlukBelgeDAO() {
        // Yapıcı metotta genellikle DAO nesnesi başlatılır,
        // ancak DatabaseConnection statik olduğu için burada özel bir başlatma gerekmez.
    }

    /**
     * Yeni bir staj uygunluk belgesi kaydını veritabanına ekler.
     * Belge ID'si Firebird generator'ından otomatik olarak alınır ve atanır.
     *
     * @param belge Eklenecek StajUygunlukBelge nesnesi.
     * @throws SQLException Eğer bir veritabanı hatası oluşursa (örn. bağlantı, SQL hatası, generator hatası).
     */
    public void addStajUygunlukBelge(StajUygunlukBelge belge) throws SQLException {
        // SQL sorgusu, BELGE_ID'yi de içeriyor çünkü biz kendimiz generator'dan alıp atayacağız.
        String sql = "INSERT INTO STAJ_UYGUNLUK_BELGE (BELGE_ID, STAJYER_ID, SEHIR, FAKULTE, OGRENCI_NO) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Firebird Generator'dan yeni bir ID değeri al
            int newBelgeId;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT NEXT VALUE FOR GEN_BELGE_ID FROM RDB$DATABASE")) { // Generator adı doğru olmalı
                if (rs.next()) {
                    newBelgeId = rs.getInt(1); // İlk sütundaki değeri al (NEXT VALUE)
                } else {
                    // Generator'dan ID alınamazsa istisna fırlat
                    throw new SQLException("GEN_BELGE_ID generator'ından yeni ID alınamadı.");
                }
            }
            belge.setBelgeId(newBelgeId); // Oluşturulan ID'yi StajUygunlukBelge nesnesine ata

            // 2. PreparedStatement kullanarak belge bilgilerini veritabanına ekle
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, belge.getBelgeId());      // BELGE_ID
                ps.setInt(2, belge.getStajyerId());    // STAJYER_ID (ilişkili stajyerin ID'si)
                ps.setString(3, belge.getSehir());     // SEHIR
                ps.setString(4, belge.getFakulte());   // FAKULTE
                ps.setString(5, belge.getOgrenciNo()); // OGRENCI_NO

                int affectedRows = ps.executeUpdate(); // Sorguyu çalıştır
                if (affectedRows > 0) {
                    LOGGER.log(Level.INFO, "Staj uygunluk belgesi başarıyla veritabanına eklendi. ID: {0}", newBelgeId);
                } else {
                    LOGGER.log(Level.WARNING, "Staj uygunluk belgesi eklenemedi, hiçbir satır etkilenmedi. ID: {0}", newBelgeId);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Staj uygunluk belgesi eklenirken kritik bir veritabanı hatası oluştu: " + e.getMessage(), e);
            throw e; // Hatayı çağrı yapan metoda fırlat
        }
    }

    /**
     * Verilen stajyer ID'sine göre staj uygunluk belgesi kaydını veritabanından getirir.
     *
     * @param stajyerId Aranacak stajyerin ID'si.
     * @return Bulunan StajUygunlukBelge nesnesi veya bulunamazsa null.
     * @throws SQLException Eğer bir veritabanı hatası oluşursa.
     */
    public StajUygunlukBelge getStajUygunlukBelgeByStajyerId(int stajyerId) throws SQLException {
        String sql = "SELECT BELGE_ID, STAJYER_ID, SEHIR, FAKULTE, OGRENCI_NO FROM STAJ_UYGUNLUK_BELGE WHERE STAJYER_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stajyerId); // Parametre olarak stajyerId'yi ata
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // ResultSet'ten verileri alıp StajUygunlukBelge nesnesine doldur
                    StajUygunlukBelge belge = new StajUygunlukBelge();
                    belge.setBelgeId(rs.getInt("BELGE_ID"));
                    belge.setStajyerId(rs.getInt("STAJYER_ID"));
                    belge.setSehir(rs.getString("SEHIR"));
                    belge.setFakulte(rs.getString("FAKULTE"));
                    belge.setOgrenciNo(rs.getString("OGRENCI_NO"));
                    LOGGER.log(Level.INFO, "Stajyer ID {0} için staj uygunluk belgesi bulundu.", stajyerId);
                    return belge;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Stajyer ID " + stajyerId + " için staj uygunluk belgesi getirilirken veritabanı hatası oluştu: " + e.getMessage(), e);
            throw e; // Hatayı çağrı yapan metoda fırlat
        }
        return null; // Kayıt bulunamazsa null döner
    }
    
    // Opsiyonel: Eğer belgede güncelleme veya silme işlemleri de yapılacaksa, 
    // updateStajUygunlukBelge ve deleteStajUygunlukBelge metotlarını da ekleyebilirsiniz.
    
    // /**
    //  * Mevcut bir staj uygunluk belgesi kaydını günceller.
    //  * @param belge Güncellenecek StajUygunlukBelge nesnesi.
    //  * @return İşlem başarılı ise true, aksi takdirde false.
    //  * @throws SQLException Eğer bir veritabanı hatası oluşursa.
    //  */
    // public boolean updateStajUygunlukBelge(StajUygunlukBelge belge) throws SQLException {
    //     String sql = "UPDATE STAJ_UYGUNLUK_BELGE SET STAJYER_ID = ?, SEHIR = ?, FAKULTE = ?, OGRENCI_NO = ? WHERE BELGE_ID = ?";
    //     try (Connection conn = DatabaseConnection.getConnection();
    //          PreparedStatement ps = conn.prepareStatement(sql)) {
    //         ps.setInt(1, belge.getStajyerId());
    //         ps.setString(2, belge.getSehir());
    //         ps.setString(3, belge.getFakulte());
    //         ps.setString(4, belge.getOgrenciNo());
    //         ps.setInt(5, belge.getBelgeId());
    //         boolean success = ps.executeUpdate() > 0;
    //         if (success) {
    //             LOGGER.log(Level.INFO, "Staj uygunluk belgesi başarıyla güncellendi. ID: {0}", belge.getBelgeId());
    //         } else {
    //             LOGGER.log(Level.WARNING, "Staj uygunluk belgesi ID {0} güncellenemedi, kayıt bulunamadı veya değişiklik yapılmadı.", belge.getBelgeId());
    //         }
    //         return success;
    //     } catch (SQLException e) {
    //         LOGGER.log(Level.SEVERE, "Staj uygunluk belgesi ID " + belge.getBelgeId() + " güncellenirken kritik bir veritabanı hatası oluştu: " + e.getMessage(), e);
    //         throw e;
    //     }
    // }

    // /**
    //  * Verilen belge ID'sine göre staj uygunluk belgesi kaydını siler.
    //  * @param belgeId Silinecek belgenin ID'si.
    //  * @return İşlem başarılı ise true, aksi takdirde false.
    //  * @throws SQLException Eğer bir veritabanı hatası oluşursa.
    //  */
    // public boolean deleteStajUygunlukBelge(int belgeId) throws SQLException {
    //     String sql = "DELETE FROM STAJ_UYGUNLUK_BELGE WHERE BELGE_ID = ?";
    //     try (Connection conn = DatabaseConnection.getConnection();
    //          PreparedStatement ps = conn.prepareStatement(sql)) {
    //         ps.setInt(1, belgeId);
    //         boolean success = ps.executeUpdate() > 0;
    //         if (success) {
    //             LOGGER.log(Level.INFO, "Staj uygunluk belgesi başarıyla silindi. ID: {0}", belgeId);
    //         } else {
    //             LOGGER.log(Level.WARNING, "Staj uygunluk belgesi ID {0} silinemedi, kayıt bulunamadı.", belgeId);
    //         }
    //         return success;
    //     } catch (SQLException e) {
    //         LOGGER.log(Level.SEVERE, "Staj uygunluk belgesi ID " + belgeId + " silinirken kritik bir veritabanı hatası oluştu: " + e.getMessage(), e);
    //         throw e;
    //     }
    // }
}