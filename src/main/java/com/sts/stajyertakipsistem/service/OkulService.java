package com.sts.stajyertakipsistem.service;

import com.sts.stajyertakipsistem.dao.OkulDAO;
import com.sts.stajyertakipsistem.model.Okul;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OkulService {

    private static final Logger LOGGER = Logger.getLogger(OkulService.class.getName());

    private OkulDAO okulDAO;

    public OkulService() {
        this.okulDAO = new OkulDAO();
    }

    public int addOkul(Okul okul) {
        if (okul == null || okul.getOkulAdi() == null || okul.getOkulAdi().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Okul nesnesi veya adı boş olamaz.");
            return -1;
        }

        try {
            int generatedId = okulDAO.addOkul(okul);
            if (generatedId != -1) {
                LOGGER.log(Level.INFO, "Okul başarıyla eklendi, ID: " + generatedId);
            } else {
                LOGGER.log(Level.WARNING, "Okul eklenirken bir sorun oluştu.");
            }
            return generatedId;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Okul eklenirken beklenmeyen bir hata oluştu.", e);
            return -1;
        }
    }

    public boolean updateOkul(Okul okul) {
        if (okul == null || okul.getOkulId() <= 0 || okul.getOkulAdi() == null || okul.getOkulAdi().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Güncellenecek okul nesnesi, ID'si veya adı geçersiz.");
            return false;
        }

        try {
            boolean success = okulDAO.updateOkul(okul);
            if (success) {
                LOGGER.log(Level.INFO, "Okul başarıyla güncellendi, ID: " + okul.getOkulId());
            } else {
                LOGGER.log(Level.WARNING, "Okul güncellenirken bir sorun oluştu, ID: " + okul.getOkulId());
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Okul güncellenirken beklenmeyen bir hata oluştu, ID: " + okul.getOkulId(), e);
            return false;
        }
    }

    public Okul getOkulById(int okulId) {
        if (okulId <= 0) {
            LOGGER.log(Level.WARNING, "Okul ID geçerli değil: " + okulId);
            return null;
        }
        try {
            Okul okul = okulDAO.getOkulById(okulId);
            if (okul == null) {
                LOGGER.log(Level.INFO, "Okul bulunamadı, ID: " + okulId);
            }
            return okul;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Okul getirilirken beklenmeyen bir hata oluştu, ID: " + okulId, e);
            return null;
        }
    }

    public List<Okul> getAllOkullar() {
        try {
            return okulDAO.getAllOkullar();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Tüm okullar getirilirken beklenmeyen bir hata oluştu.", e);
            return List.of();
        }
    }

    public boolean deleteOkul(int okulId) {
        if (okulId <= 0) {
            LOGGER.log(Level.WARNING, "Silinecek okul ID'si geçerli değil: " + okulId);
            return false;
        }

        try {
            boolean success = okulDAO.deleteOkul(okulId);
            if (success) {
                LOGGER.log(Level.INFO, "Okul başarıyla silindi, ID: " + okulId);
            } else {
                LOGGER.log(Level.WARNING, "Okul silinirken bir sorun oluştu, ID: " + okulId);
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Okul silinirken beklenmeyen bir hata oluştu, ID: " + okulId, e);
            return false;
        }
    }
}