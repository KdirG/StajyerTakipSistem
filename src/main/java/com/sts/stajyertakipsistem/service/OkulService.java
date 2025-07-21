package com.sts.stajyertakipsistem.service;

import com.sts.stajyertakipsistem.dao.OkulDAO;
import com.sts.stajyertakipsistem.model.Okul;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OkulService {

    private static final Logger LOGGER = Logger.getLogger(OkulService.class.getName());
    private final OkulDAO okulDAO;

    public OkulService() {
        this.okulDAO = new OkulDAO();
    }

    public boolean addOkul(Okul okul) {
        if (okul == null || okul.getOkulAdi() == null || okul.getOkulAdi().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Eklenecek okul nesnesi veya adı geçersiz.");
            return false;
        }
        // DAO'dan dönen ID'nin 0'dan büyük olup olmadığını kontrol et.
        // DAO, yeni ID'yi okul nesnesinin üzerine zaten atıyor.
        return okulDAO.addOkul(okul) > 0;
    }

    public boolean updateOkul(Okul okul) {
        if (okul == null || okul.getOkulId() <= 0) {
            LOGGER.log(Level.WARNING, "Güncellenecek okul nesnesi veya ID'si geçersiz.");
            return false;
        }
        return okulDAO.updateOkul(okul);
    }
    
    public Okul getOkulById(int okulId) {
        return okulDAO.getOkulById(okulId);
    }
    
    public List<Okul> getAllOkullar() {
        return okulDAO.getAllOkullar();
    }
    
    public boolean deleteOkul(int okulId) {
        return okulDAO.deleteOkul(okulId);
    }
}