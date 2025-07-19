package com.sts.stajyertakipsistem.service;

import com.sts.stajyertakipsistem.dao.ReferansDAO;
import com.sts.stajyertakipsistem.model.Referans;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReferansService {

    private static final Logger LOGGER = Logger.getLogger(ReferansService.class.getName());

    private ReferansDAO referansDAO;

    public ReferansService() {
        this.referansDAO = new ReferansDAO();
    }

    public boolean addReferans(Referans referans) {
        if (referans == null || referans.getAdSoyad() == null || referans.getAdSoyad().trim().isEmpty() ||
            referans.getKurum() == null || referans.getKurum().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Referans nesnesi, Ad Soyad veya Kurum boş olamaz.");
            return false;
        }
      

        try {
            boolean success = referansDAO.addReferans(referans);
            if (success) {
                LOGGER.log(Level.INFO, "Referans başarıyla eklendi, ID: " + referans.getReferansId());
            } else {
                LOGGER.log(Level.WARNING, "Referans eklenirken bir sorun oluştu.");
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Referans eklenirken beklenmeyen bir hata oluştu.", e);
            return false;
        }
    }

    public boolean updateReferans(Referans referans) {
        if (referans == null || referans.getReferansId() == null || referans.getReferansId().isEmpty() ||
            referans.getAdSoyad() == null || referans.getAdSoyad().trim().isEmpty() ||
            referans.getKurum() == null || referans.getKurum().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Güncellenecek referans nesnesi, ID'si, Ad Soyad veya Kurum geçersiz.");
            return false;
        }

        try {
            boolean success = referansDAO.updateReferans(referans);
            if (success) {
                LOGGER.log(Level.INFO, "Referans başarıyla güncellendi, ID: " + referans.getReferansId());
            } else {
                LOGGER.log(Level.WARNING, "Referans güncellenirken bir sorun oluştu, ID: " + referans.getReferansId());
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Referans güncellenirken beklenmeyen bir hata oluştu, ID: " + referans.getReferansId(), e);
            return false;
        }
    }

    public Referans getReferansById(String referansId) {
        if (referansId == null || referansId.isEmpty()) {
            LOGGER.log(Level.WARNING, "Referans ID geçerli değil: " + referansId);
            return null;
        }
        try {
            Referans referans = referansDAO.getReferansById(referansId);
            if (referans == null) {
                LOGGER.log(Level.INFO, "Referans bulunamadı, ID: " + referansId);
            }
            return referans;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Referans getirilirken beklenmeyen bir hata oluştu, ID: " + referansId, e);
            return null;
        }
    }

    public List<Referans> getAllReferanslar() {
        try {
            return referansDAO.getAllReferanslar();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Tüm referanslar getirilirken beklenmeyen bir hata oluştu.", e);
            return List.of();
        }
    }

    public boolean deleteReferans(String referansId) {
        if (referansId == null || referansId.isEmpty()) {
            LOGGER.log(Level.WARNING, "Silinecek referans ID'si geçerli değil: " + referansId);
            return false;
        }
        

        try {
            boolean success = referansDAO.deleteReferans(referansId);
            if (success) {
                LOGGER.log(Level.INFO, "Referans başarıyla silindi, ID: " + referansId);
            } else {
                LOGGER.log(Level.WARNING, "Referans silinirken bir sorun oluştu, ID: " + referansId);
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Referans silinirken beklenmeyen bir hata oluştu, ID: " + referansId, e);
            return false;
        }
    }

    public List<Referans> searchReferanslar(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllReferanslar();
        }
        try {
            return referansDAO.searchReferanslar(searchText);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Referanslar aranırken beklenmeyen bir hata oluştu.", e);
            return List.of();
        }
    }
}