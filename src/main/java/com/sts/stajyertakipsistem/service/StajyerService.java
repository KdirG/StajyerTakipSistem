package com.sts.stajyertakipsistem.service;

import com.sts.stajyertakipsistem.dao.StajyerDAO;
import com.sts.stajyertakipsistem.dao.OkulDAO;
import com.sts.stajyertakipsistem.dao.ReferansDAO;
import com.sts.stajyertakipsistem.dao.EvrakDAO;

import com.sts.stajyertakipsistem.model.Stajyer;
import com.sts.stajyertakipsistem.model.Okul; 
import com.sts.stajyertakipsistem.model.Referans; 
import com.sts.stajyertakipsistem.model.Evrak;
import com.sts.stajyertakipsistem.model.GirisEvrak;
import com.sts.stajyertakipsistem.model.CikisEvrak;
import com.sts.stajyertakipsistem.GUI.SpesifikStajyerForm; 

import java.util.List;
import java.util.UUID; 
import java.util.logging.Level;
import java.util.logging.Logger;

public class StajyerService {

    private static final Logger LOGGER = Logger.getLogger(StajyerService.class.getName());

    private StajyerDAO stajyerDAO;
    private OkulDAO okulDAO;
    private ReferansDAO referansDAO;
    private EvrakDAO evrakDAO;


    public StajyerService() {
        
        this.stajyerDAO = new StajyerDAO(); 
        this.okulDAO = new OkulDAO();       
        this.referansDAO = new ReferansDAO(); 
        this.evrakDAO = new EvrakDAO();    
    }

    public EvrakDAO getEvrakDAO() {
        return evrakDAO;
    }

    // Bu metodun StajyerDAO'da getStajyerById olması beklenir
    public Stajyer getStajyerById(String stajyerId) {
        return stajyerDAO.getStajyerById(stajyerId);
    }

    public List<Stajyer> getAllStajyerler() {
        return stajyerDAO.getAllStajyerler();
    }

    public List<Stajyer> searchStajyerler(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Arama metni boş veya null. Tüm stajyerler döndürülüyor.");
            return getAllStajyerler();
        }
        return stajyerDAO.searchStajyerler(searchText);
    }

    public boolean addStajyer(Stajyer stajyer) {
        if (stajyer == null) {
            LOGGER.log(Level.WARNING, "Eklenecek stajyer nesnesi null.");
            return false;
        }
        
        if (stajyer.getStajyerId() == null || stajyer.getStajyerId().isEmpty()) {
            stajyer.setStajyerId(UUID.randomUUID().toString());
        }
        if (stajyer.getAdSoyad() == null || stajyer.getAdSoyad().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Stajyer adı ve soyadı boş olamaz.");
            return false;
        }
        
        if (stajyer.getTcKimlik() <= 0 || String.valueOf(stajyer.getTcKimlik()).length() != 11) {
            LOGGER.log(Level.WARNING, "TC Kimlik numarası geçersiz (sıfır, negatif veya 11 haneli değil).");
            return false;
        }

        try {
      
            if (stajyer.getGirisEvrak() != null) {
                if (!stajyer.getGirisEvrak().isValid()) {
                    LOGGER.log(Level.WARNING, "Giriş evrakı geçerli değil (dosya yolu boş): " + stajyer.getGirisEvrak().getEvrakId());
                    return false;
                }
                
                String girisEvrakId = evrakDAO.addEvrak(stajyer.getGirisEvrak()); // Yeni ID'yi alır veya mevcut ID'yi korur.
                if (girisEvrakId == null) {
                    LOGGER.log(Level.SEVERE, "Giriş evrağı veritabanına kaydedilemedi.");
                    return false;
                }
                stajyer.getGirisEvrak().setEvrakId(girisEvrakId); // DAO'dan dönen ID'yi stajyer objesine set et.
            }

            // Çıkış Evrakı Kontrolü ve Kaydetme/Güncelleme
            if (stajyer.getCikisEvrak() != null) {
                if (!stajyer.getCikisEvrak().isValid()) {
                    LOGGER.log(Level.WARNING, "Çıkış evrakı geçerli değil (dosya yolu boş): " + stajyer.getCikisEvrak().getEvrakId());
                    return false;
                }
                String cikisEvrakId = evrakDAO.addEvrak(stajyer.getCikisEvrak()); // Yeni ID'yi alır veya mevcut ID'yi korur.
                if (cikisEvrakId == null) {
                    LOGGER.log(Level.SEVERE, "Çıkış evrağı veritabanına kaydedilemedi.");
                    return false;
                }
                stajyer.getCikisEvrak().setEvrakId(cikisEvrakId); // DAO'dan dönen ID'yi stajyer objesine set et.
            }
            
            // Stajyer nesnesini kaydet
            boolean result = stajyerDAO.addStajyer(stajyer);
            if (result) {
                LOGGER.log(Level.INFO, "Stajyer başarıyla eklendi: " + stajyer.getAdSoyad() + ", ID: " + stajyer.getStajyerId());
            } else {
                LOGGER.log(Level.WARNING, "Stajyer eklenirken bir sorun oluştu: " + stajyer.getAdSoyad());
            }
            return result;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Stajyer eklenirken beklenmeyen bir hata oluştu: " + stajyer.getAdSoyad(), e);
            return false;
        }
    }

    public boolean updateStajyer(Stajyer stajyer) {
        if (stajyer == null || stajyer.getStajyerId() == null || stajyer.getStajyerId().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Güncellenecek stajyer nesnesi veya ID'si geçersiz.");
            return false;
        }
         if (stajyer.getAdSoyad() == null || stajyer.getAdSoyad().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Stajyer adı ve soyadı boş olamaz.");
            return false;
        }
        if (stajyer.getTcKimlik() <= 0 || String.valueOf(stajyer.getTcKimlik()).length() != 11) {
            LOGGER.log(Level.WARNING, "TC Kimlik numarası geçersiz.");
            return false;
        }

        try {
            
            if (stajyer.getGirisEvrak() != null) {
                if (!stajyer.getGirisEvrak().isValid()) {
                    LOGGER.log(Level.WARNING, "Giriş evrakı geçerli değil (dosya yolu boş): " + stajyer.getGirisEvrak().getEvrakId());
                    return false;
                }
                
                if (stajyer.getGirisEvrak().getEvrakId() != null && !stajyer.getGirisEvrak().getEvrakId().isEmpty()) {
                    boolean updated = evrakDAO.updateEvrak(stajyer.getGirisEvrak());
                    if (!updated) {
                        LOGGER.log(Level.SEVERE, "Mevcut giriş evrağı güncellenemedi: " + stajyer.getGirisEvrak().getEvrakId());
                        return false;
                    }
                } else {
                    String newEvrakId = evrakDAO.addEvrak(stajyer.getGirisEvrak());
                    if (newEvrakId == null) {
                        LOGGER.log(Level.SEVERE, "Yeni giriş evrağı veritabanına kaydedilemedi.");
                        return false;
                    }
                    stajyer.getGirisEvrak().setEvrakId(newEvrakId); // Yeni oluşan ID'yi set et
                }
            } else {
                
            }

            
            if (stajyer.getCikisEvrak() != null) {
                if (!stajyer.getCikisEvrak().isValid()) {
                    LOGGER.log(Level.WARNING, "Çıkış evrakı geçerli değil (dosya yolu boş): " + stajyer.getCikisEvrak().getEvrakId());
                    return false;
                }
                
                if (stajyer.getCikisEvrak().getEvrakId() != null && !stajyer.getCikisEvrak().getEvrakId().isEmpty()) {
                    boolean updated = evrakDAO.updateEvrak(stajyer.getCikisEvrak());
                    if (!updated) {
                        LOGGER.log(Level.SEVERE, "Mevcut çıkış evrağı güncellenemedi: " + stajyer.getCikisEvrak().getEvrakId());
                        return false;
                    }
                } else {
                    String newEvrakId = evrakDAO.addEvrak(stajyer.getCikisEvrak());
                    if (newEvrakId == null) {
                        LOGGER.log(Level.SEVERE, "Yeni çıkış evrağı veritabanına kaydedilemedi.");
                        return false;
                    }
                    stajyer.getCikisEvrak().setEvrakId(newEvrakId); 
                }
            } else {
                
            }

            
            boolean result = stajyerDAO.updateStajyer(stajyer);
            if (result) {
                LOGGER.log(Level.INFO, "Stajyer başarıyla güncellendi: " + stajyer.getAdSoyad() + ", ID: " + stajyer.getStajyerId());
            } else {
                LOGGER.log(Level.WARNING, "Stajyer güncellenirken bir sorun oluştu: " + stajyer.getAdSoyad());
            }
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Stajyer güncellenirken beklenmeyen bir hata oluştu: " + stajyer.getAdSoyad(), e);
            return false;
        }
    }

    public boolean deleteStajyer(String stajyerId) {
        if (stajyerId == null || stajyerId.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Silinecek stajyer ID'si boş veya null.");
            return false;
        }
        try {
            Stajyer stajyerToDelete = stajyerDAO.getStajyerById(stajyerId);
            if (stajyerToDelete != null) {
                if (stajyerToDelete.getGirisEvrak() != null) {
                    
                    evrakDAO.deleteEvrak(stajyerToDelete.getGirisEvrak().getEvrakId());
                    LOGGER.log(Level.INFO, "Giriş evrağı silindi: " + stajyerToDelete.getGirisEvrak().getEvrakId());
                }
                if (stajyerToDelete.getCikisEvrak() != null) {
                    evrakDAO.deleteEvrak(stajyerToDelete.getCikisEvrak().getEvrakId());
                    LOGGER.log(Level.INFO, "Çıkış evrağı silindi: " + stajyerToDelete.getCikisEvrak().getEvrakId());
                }
            }

            boolean result = stajyerDAO.deleteStajyer(stajyerId);
            if (result) {
                LOGGER.log(Level.INFO, "Stajyer başarıyla silindi: " + stajyerId);
            } else {
                LOGGER.log(Level.WARNING, "Stajyer silinirken bir sorun oluştu: " + stajyerId);
            }
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Stajyer silinirken beklenmeyen bir hata oluştu: " + stajyerId, e);
            return false;
        }
    }

    // SpesifikStajyerForm'dan çağrılacak olan evrak güncelleme metodu
    public boolean updateStajyerEvrak(String stajyerId, Evrak evrak, SpesifikStajyerForm.EvrakTuru evrakTuru) {
        try {
            Stajyer existingStajyer = stajyerDAO.getStajyerById(stajyerId);
            if (existingStajyer == null) {
                LOGGER.log(Level.WARNING, "updateStajyerEvrak: Stajyer bulunamadı, evrak güncellenemedi: " + stajyerId);
                return false;
            }

            
            if (evrak == null || !evrak.isValid()) {
                LOGGER.log(Level.WARNING, "updateStajyerEvrak: Sağlanan evrak nesnesi null veya geçerli değil (dosya yolu boş).");
                return false;
            }

            
            String savedEvrakId;
            if (evrak.getEvrakId() != null && !evrak.getEvrakId().isEmpty()) {
                // Evrak ID'si varsa, mevcut evrakı güncelle
                boolean updated = evrakDAO.updateEvrak(evrak);
                if (!updated) {
                    LOGGER.log(Level.SEVERE, "updateStajyerEvrak: Mevcut evrak güncellenemedi: " + evrak.getEvrakId());
                    return false;
                }
                savedEvrakId = evrak.getEvrakId();
            } else {
                // Evrak ID'si yoksa, yeni bir evrak olarak ekle
                // EvrakDAO.addEvrak() metodunuzun Evrak'ın ID'sini String olarak döndürdüğünü varsayıyorum.
                savedEvrakId = evrakDAO.addEvrak(evrak);
                if (savedEvrakId == null) {
                    LOGGER.log(Level.SEVERE, "updateStajyerEvrak: Yeni evrak veritabanına kaydedilemedi.");
                    return false;
                }
                evrak.setEvrakId(savedEvrakId); 
            }

            
            if (evrakTuru == SpesifikStajyerForm.EvrakTuru.GIRIS) {
                if (evrak instanceof GirisEvrak) {
                    existingStajyer.setGirisEvrak((GirisEvrak) evrak);
                } else {
                    LOGGER.log(Level.WARNING, "updateStajyerEvrak: Giriş evrakı beklenirken farklı bir evrak türü geldi: " + evrak.getClass().getName());
                    return false;
                }
            } else if (evrakTuru == SpesifikStajyerForm.EvrakTuru.CIKIS) {
                if (evrak instanceof CikisEvrak) {
                    existingStajyer.setCikisEvrak((CikisEvrak) evrak);
                } else {
                    LOGGER.log(Level.WARNING, "updateStajyerEvrak: Çıkış evrakı beklenirken farklı bir evrak türü geldi: " + evrak.getClass().getName());
                    return false;
                }
            } else {
                LOGGER.log(Level.WARNING, "updateStajyerEvrak: Bilinmeyen evrak türü: " + evrakTuru);
                return false;
            }

            
            boolean success = stajyerDAO.updateStajyer(existingStajyer);
            if (success) {
                LOGGER.log(Level.INFO, evrakTuru + " evrakı stajyere başarıyla bağlandı ve güncellendi: Stajyer ID=" + stajyerId);
            } else {
                LOGGER.log(Level.WARNING, evrakTuru + " evrakı stajyere bağlanırken/güncellenirken sorun oluştu: Stajyer ID=" + stajyerId);
            }
            return success;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "updateStajyerEvrak: Stajyer evrakı güncellenirken beklenmeyen hata: Stajyer ID=" + stajyerId, e);
            return false;
        }
    }

    public Stajyer getStajyerId(String currentStajyerId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}