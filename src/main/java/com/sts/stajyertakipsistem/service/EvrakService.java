package com.sts.stajyertakipsistem.service;

import com.sts.stajyertakipsistem.dao.EvrakDAO;
import com.sts.stajyertakipsistem.model.Evrak;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvrakService {

    private static final Logger LOGGER = Logger.getLogger(EvrakService.class.getName());
    private final EvrakDAO evrakDAO;

    public EvrakService() {
        this.evrakDAO = new EvrakDAO();
    }

    public boolean addEvrak(Evrak evrak) {
        if (evrak == null || evrak.getDosyaYolu() == null || evrak.getDosyaYolu().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Eklenecek evrak nesnesi veya dosya yolu geçersiz.");
            return false;
        }
        // DAO'dan dönen ID'nin 0'dan büyük olup olmadığını kontrol et.
        return evrakDAO.addEvrak(evrak) > 0;
    }

    public boolean updateEvrak(Evrak evrak) {
        if (evrak == null || evrak.getEvrakId() <= 0) {
            LOGGER.log(Level.WARNING, "Güncellenecek evrak nesnesi veya ID'si geçersiz.");
            return false;
        }
        return evrakDAO.updateEvrak(evrak);
    }
    
    public Evrak getEvrakById(int evrakId) {
        return evrakDAO.getEvrakById(evrakId);
    }

    public boolean deleteEvrak(int evrakId) {
        return evrakDAO.deleteEvrak(evrakId);
    }
}