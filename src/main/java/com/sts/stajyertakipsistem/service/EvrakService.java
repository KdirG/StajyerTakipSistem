package com.sts.stajyertakipsistem.service;

import com.sts.stajyertakipsistem.dao.EvrakDAO;
import com.sts.stajyertakipsistem.model.Evrak;
import com.sts.stajyertakipsistem.model.GirisEvrak;
import com.sts.stajyertakipsistem.model.CikisEvrak;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EvrakService {

    private static final Logger LOGGER = Logger.getLogger(EvrakService.class.getName());

    private EvrakDAO evrakDAO;

    public EvrakService() {
        this.evrakDAO = new EvrakDAO();
    }

    // Evrak ekleme metodu
    public String addEvrak(Evrak evrak) {
        if (evrak == null || !evrak.isValid()) {
            LOGGER.log(Level.WARNING, "Eklenecek evrak nesnesi null veya geçerli değil.");
            return null;
        }
        // Ek iş mantığı/validasyonlar buraya gelebilir (örn. dosya formatı kontrolü)

        try {
            String generatedId = evrakDAO.addEvrak(evrak);
            if (generatedId != null) {
                evrak.setEvrakId(generatedId); // DAO'dan dönen ID'yi modele set et
                LOGGER.log(Level.INFO, "Evrak başarıyla eklendi, ID: " + generatedId);
            } else {
                LOGGER.log(Level.WARNING, "Evrak eklenirken bir sorun oluştu.");
            }
            return generatedId;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Evrak eklenirken beklenmeyen bir hata oluştu.", e);
            return null;
        }
    }

    // Evrak güncelleme metodu
    public boolean updateEvrak(Evrak evrak) {
        if (evrak == null || evrak.getEvrakId() == null || evrak.getEvrakId().trim().isEmpty() || !evrak.isValid()) {
            LOGGER.log(Level.WARNING, "Güncellenecek evrak nesnesi, ID'si null/boş veya geçerli değil.");
            return false;
        }
        // Ek iş mantığı/validasyonlar buraya gelebilir

        try {
            boolean success = evrakDAO.updateEvrak(evrak);
            if (success) {
                LOGGER.log(Level.INFO, "Evrak başarıyla güncellendi, ID: " + evrak.getEvrakId());
            } else {
                LOGGER.log(Level.WARNING, "Evrak güncellenirken bir sorun oluştu, ID: " + evrak.getEvrakId());
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Evrak güncellenirken beklenmeyen bir hata oluştu, ID: " + evrak.getEvrakId(), e);
            return false;
        }
    }

    // Evrak alma metodu (EvrakTuru ile birlikte)
    public Evrak getEvrakById(String evrakId, Class<? extends Evrak> evrakClass) {
        if (evrakId == null || evrakId.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Evrak ID'si boş veya null.");
            return null;
        }
        try {
            Evrak evrak = evrakDAO.getEvrakById(evrakId);
            if (evrak != null && evrakClass.isInstance(evrak)) {
                LOGGER.log(Level.INFO, "Evrak başarıyla getirildi, ID: " + evrakId);
                return evrak;
            } else if (evrak != null && !evrakClass.isInstance(evrak)) {
                LOGGER.log(Level.WARNING, "Evrak bulundu ancak beklenen türde değil. ID: " + evrakId + ", Beklenen: " + evrakClass.getName() + ", Bulunan: " + evrak.getClass().getName());
                return null; // Tür uyuşmazlığı varsa null döndür
            } else {
                LOGGER.log(Level.WARNING, "Evrak bulunamadı, ID: " + evrakId);
                return null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Evrak getirilirken beklenmeyen bir hata oluştu, ID: " + evrakId, e);
            return null;
        }
    }


    // Evrak silme metodu
    public boolean deleteEvrak(String evrakId) {
        if (evrakId == null || evrakId.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Silinecek evrak ID'si boş veya null.");
            return false;
        }
        try {
            boolean success = evrakDAO.deleteEvrak(evrakId);
            if (success) {
                LOGGER.log(Level.INFO, "Evrak başarıyla silindi, ID: " + evrakId);
            } else {
                LOGGER.log(Level.WARNING, "Evrak silinirken bir sorun oluştu, ID: " + evrakId);
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Evrak silinirken beklenmeyen bir hata oluştu, ID: " + evrakId, e);
            return false;
        }
    }
}