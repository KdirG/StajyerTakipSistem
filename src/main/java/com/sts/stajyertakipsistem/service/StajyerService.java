package com.sts.stajyertakipsistem.service;

import com.sts.stajyertakipsistem.dao.StajyerDAO;
import com.sts.stajyertakipsistem.dao.OkulDAO;
import com.sts.stajyertakipsistem.dao.ReferansDAO;
import com.sts.stajyertakipsistem.dao.EvrakDAO;
import com.sts.stajyertakipsistem.model.Stajyer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StajyerService {

    private static final Logger LOGGER = Logger.getLogger(StajyerService.class.getName());

    private final StajyerDAO stajyerDAO;
    private final OkulDAO okulDAO;
    private final ReferansDAO referansDAO;
    private final EvrakDAO evrakDAO;

    public StajyerService() {
        this.stajyerDAO = new StajyerDAO();
        this.okulDAO = new OkulDAO();
        this.referansDAO = new ReferansDAO();
        this.evrakDAO = new EvrakDAO();
    }

    // İlişkili nesneleri (Okul, Referans, Evrak) kaydeder veya günceller.
    private void saveOrUpdateRelatedObjects(Stajyer stajyer) {
        if (stajyer.getOkul() != null && stajyer.getOkul().getOkulAdi() != null && !stajyer.getOkul().getOkulAdi().isEmpty()) {
            if (stajyer.getOkul().getOkulId() == 0) okulDAO.addOkul(stajyer.getOkul());
            else okulDAO.updateOkul(stajyer.getOkul());
        }
        if (stajyer.getReferans() != null && stajyer.getReferans().getAdSoyad() != null && !stajyer.getReferans().getAdSoyad().isEmpty()) {
            if (stajyer.getReferans().getReferansId() == 0) referansDAO.addReferans(stajyer.getReferans());
            else referansDAO.updateReferans(stajyer.getReferans());
        }
        if (stajyer.getGirisEvrak() != null && stajyer.getGirisEvrak().getDosyaYolu() != null) {
            if (stajyer.getGirisEvrak().getEvrakId() == 0) evrakDAO.addEvrak(stajyer.getGirisEvrak());
            else evrakDAO.updateEvrak(stajyer.getGirisEvrak());
        }
        if (stajyer.getCikisEvrak() != null && stajyer.getCikisEvrak().getDosyaYolu() != null) {
            if (stajyer.getCikisEvrak().getEvrakId() == 0) evrakDAO.addEvrak(stajyer.getCikisEvrak());
            else evrakDAO.updateEvrak(stajyer.getCikisEvrak());
        }
    }

    public boolean addStajyer(Stajyer stajyer) {
        try {
            // Önce ilişkili nesneleri kaydet ki ID'lerini alsınlar.
            saveOrUpdateRelatedObjects(stajyer);
            // Sonra stajyerin kendisini kaydet.
            return stajyerDAO.addStajyer(stajyer);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Stajyer eklenirken iş akışı hatası.", e);
            return false;
        }
    }

    public boolean updateStajyer(Stajyer stajyer) {
         try {
            // Önce ilişkili nesneleri güncelle/kaydet.
            saveOrUpdateRelatedObjects(stajyer);
            // Sonra stajyerin kendisini güncelle.
            return stajyerDAO.updateStajyer(stajyer);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Stajyer güncellenirken iş akışı hatası.", e);
            return false;
        }
    }

    public boolean deleteStajyer(int stajyerId) {
        try {
            Stajyer stajyer = stajyerDAO.getStajyerById(stajyerId);
            if (stajyer == null) return false;

            // Önce ana kaydı sil.
            boolean success = stajyerDAO.deleteStajyer(stajyerId);
            
            // Eğer ana kayıt başarıyla silindiyse, ilişkili kayıtları sil.
            if (success) {
                if (stajyer.getGirisEvrak() != null) evrakDAO.deleteEvrak(stajyer.getGirisEvrak().getEvrakId());
                if (stajyer.getCikisEvrak() != null) evrakDAO.deleteEvrak(stajyer.getCikisEvrak().getEvrakId());
                if (stajyer.getReferans() != null) referansDAO.deleteReferans(stajyer.getReferans().getReferansId());
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Stajyer silinirken hata.", e);
            return false;
        }
    }
    
    public Stajyer getStajyerById(int stajyerId) {
        return stajyerDAO.getStajyerById(stajyerId);
    }

    public List<Stajyer> getAllStajyerler() {
        return stajyerDAO.getAllStajyerler();
    }
}