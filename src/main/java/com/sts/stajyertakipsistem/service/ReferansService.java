package com.sts.stajyertakipsistem.service;

import com.sts.stajyertakipsistem.dao.ReferansDAO;
import com.sts.stajyertakipsistem.model.Referans;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReferansService {

    private static final Logger LOGGER = Logger.getLogger(ReferansService.class.getName());
    private final ReferansDAO referansDAO;

    public ReferansService() {
        this.referansDAO = new ReferansDAO();
    }
    
    public boolean addReferans(Referans referans) {
        if (referans == null || referans.getAdSoyad() == null || referans.getAdSoyad().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Eklenecek referans nesnesi veya adı geçersiz.");
            return false;
        }
        // DAO'dan dönen ID'nin 0'dan büyük olup olmadığını kontrol et.
        return referansDAO.addReferans(referans) > 0;
    }

    public boolean updateReferans(Referans referans) {
        if (referans == null || referans.getReferansId() <= 0) {
            LOGGER.log(Level.WARNING, "Güncellenecek referans nesnesi veya ID'si geçersiz.");
            return false;
        }
        return referansDAO.updateReferans(referans);
    }
    
    public Referans getReferansById(int referansId) {
        return referansDAO.getReferansById(referansId);
    }
    
    public boolean deleteReferans(int referansId) {
        return referansDAO.deleteReferans(referansId);
    }
}