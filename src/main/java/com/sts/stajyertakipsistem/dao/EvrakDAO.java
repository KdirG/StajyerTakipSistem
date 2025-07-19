package com.sts.stajyertakipsistem.dao;

import com.sts.stajyertakipsistem.model.Evrak;
import com.sts.stajyertakipsistem.model.GirisEvrak;
import com.sts.stajyertakipsistem.model.CikisEvrak;
import com.sts.stajyertakipsistem.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID; // For generating unique IDs

public class EvrakDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    /**
     * Yeni bir Evrak kaydını veritabanına ekler ve oluşturulan ID'yi döndürür.
     * Eğer evrak nesnesinin EvrakId'si zaten varsa, onu kullanır.
     *
     * @param evrak Eklenecek Evrak nesnesi (GirisEvrak veya CikisEvrak olabilir).
     * @return Başarıyla eklenirse oluşturulan evrak ID'si (String), aksi halde null.
     */
    public String addEvrak(Evrak evrak) {
        String sql = "INSERT INTO EVRAKLAR (EVRAK_ID, DOSYA_YOLU, EVRAK_TURU) VALUES (?, ?, ?)";
        String generatedId = evrak.getEvrakId(); // Modelden gelen ID'yi kullanma önceliği

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (generatedId == null || generatedId.isEmpty()) {
                generatedId = UUID.randomUUID().toString(); // Eğer ID yoksa yeni üret
                evrak.setEvrakId(generatedId); // Model nesnesine de seti et
            }

            ps.setString(1, generatedId);
            ps.setString(2, evrak.getDosyayolu());
            // EVRAK_TURU sütunu ekledim. Bu, okunurken doğru alt sınıfı oluşturmak için kullanılacak.
            // getEvrakTuruAciklama() metodunu kullanabiliriz, veya daha kısa bir kod (örn: "GIRIS", "CIKIS")
            // kullanılabilir. Burada getEvrakTuruAciklama() kullanılıyor.
            ps.setString(3, evrak.getEvrakTuruAciklama());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                return generatedId;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Loglama veya hata fırlatma yapılabilir
        }
        return null;
    }

    /**
     * Mevcut bir Evrak kaydını veritabanında günceller.
     *
     * @param evrak Güncellenecek Evrak nesnesi. EvrakId'si boş olmamalıdır.
     * @return Güncelleme başarılıysa true, aksi halde false.
     */
    public boolean updateEvrak(Evrak evrak) {
        if (evrak.getEvrakId() == null || evrak.getEvrakId().isEmpty()) {
            // Log this: "Evrak ID boş olduğu için güncelleme yapılamadı."
            return false;
        }

        String sql = "UPDATE EVRAKLAR SET DOSYA_YOLU = ?, EVRAK_TURU = ? WHERE EVRAK_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, evrak.getDosyayolu());
            ps.setString(2, evrak.getEvrakTuruAciklama()); // Türü de güncelleyebiliriz, gerçi genelde sabit kalır
            ps.setString(3, evrak.getEvrakId());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Evrak ID'sine göre Evrak kaydını veritabanından getirir.
     * Gelen EVRAK_TURU sütununa göre doğru alt sınıf (GirisEvrak veya CikisEvrak) oluşturulur.
     *
     * @param evrakId Getirilecek evrakın ID'si.
     * @return Bulunursa ilgili Evrak nesnesi (GirisEvrak veya CikisEvrak), aksi halde null.
     */
    public Evrak getEvrakById(String evrakId) {
        Evrak evrak = null;
        String sql = "SELECT EVRAK_ID, DOSYA_YOLU, EVRAK_TURU FROM EVRAKLAR WHERE EVRAK_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, evrakId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String fetchedEvrakId = rs.getString("EVRAK_ID");
                    String dosyaYolu = rs.getString("DOSYA_YOLU");
                    String evrakTuru = rs.getString("EVRAK_TURU"); // Evrak türünü okuyalım

                    // Okunan türe göre doğru alt sınıfı oluştur
                    if ("Giriş Evrağı".equals(evrakTuru)) { // Giriş Evrakı'nın getEvrakTuruAciklama() değeri
                        evrak = new GirisEvrak(fetchedEvrakId, dosyaYolu);
                    } else if ("Çıkış Evrağı".equals(evrakTuru)) { // Çıkış Evrakı'nın getEvrakTuruAciklama() değeri
                        evrak = new CikisEvrak(fetchedEvrakId, dosyaYolu);
                    } else {
                        // Bilinmeyen veya beklenmeyen bir tür gelirse, Evrak nesnesi oluşturabiliriz
                        // veya hata fırlatabiliriz, projenin ihtiyacına göre değişir.
                        // Şimdilik null bırakalım, EvrakService'de bu durum loglanır.
                        // evrak = new Evrak(fetchedEvrakId, dosyaYolu); // Eğer abstract olmasaydı.
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evrak;
    }

    /**
     * Evrak ID'sine göre bir Evrak kaydını veritabanından siler.
     *
     * @param evrakId Silinecek evrakın ID'si.
     * @return Silme başarılıysa true, aksi halde false.
     */
    public boolean deleteEvrak(String evrakId) {
        String sql = "DELETE FROM EVRAKLAR WHERE EVRAK_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, evrakId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}