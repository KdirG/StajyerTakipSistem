package com.sts.stajyertakipsistem.dao;

import com.sts.stajyertakipsistem.model.Stajyer;
import com.sts.stajyertakipsistem.db.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate; 
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StajyerDAO {

    private static final Logger LOGGER = Logger.getLogger(StajyerDAO.class.getName());
    private final OkulDAO okulDAO = new OkulDAO();
    private final ReferansDAO referansDAO = new ReferansDAO();
    private final EvrakDAO evrakDAO = new EvrakDAO();

    
    private Stajyer populateStajyerFromResultSet(ResultSet rs) throws SQLException {
        Stajyer stajyer = new Stajyer();
        stajyer.setStajyerId(rs.getInt("STAJYER_ID"));
        stajyer.setTcKimlik(rs.getString("TC_KIMLIK"));
        stajyer.setAdSoyad(rs.getString("AD_SOYAD"));
        stajyer.setAdres(rs.getString("ADRES"));
        stajyer.setTelefonNo(rs.getString("TELEFON_NO"));
        stajyer.setIbanNo(rs.getString("IBAN_NO"));
        
        Date dogumTarihiSql = rs.getDate("DOGUM_TARIHI");
        if (dogumTarihiSql != null) stajyer.setDogumTarihi(dogumTarihiSql.toLocalDate());

        Date baslangicTarihiSql = rs.getDate("BASLANGIC_TARIHI");
        if (baslangicTarihiSql != null) stajyer.setStajBaslangicTarihi(baslangicTarihiSql.toLocalDate());

        Date bitisTarihiSql = rs.getDate("BITIS_TARIHI");
        if (bitisTarihiSql != null) stajyer.setStajBitisTarihi(bitisTarihiSql.toLocalDate());

        stajyer.setBolum(rs.getString("BOLUM"));
        stajyer.setSinif(rs.getInt("SINIF"));

        
        int okulId = rs.getInt("OKUL_ID");
        if (!rs.wasNull()) { 
            stajyer.setOkul(okulDAO.getOkulById(okulId));
        } else {
            stajyer.setOkul(null); 
        }
        
        
        int referansId = rs.getInt("REFERANS_ID");
        if (!rs.wasNull()) { 
            stajyer.setReferans(referansDAO.getReferansById(referansId));
        } else {
            stajyer.setReferans(null);
        }

        
        int girisEvrakId = rs.getInt("GIRIS_EVRAK_ID");
        if (!rs.wasNull()) {
            stajyer.setGirisEvrak(evrakDAO.getEvrakById(girisEvrakId));
        } else {
            stajyer.setGirisEvrak(null);
        }

        
        int cikisEvrakId = rs.getInt("CIKIS_EVRAK_ID");
        if (!rs.wasNull()) {
            stajyer.setCikisEvrak(evrakDAO.getEvrakById(cikisEvrakId));
        } else {
            stajyer.setCikisEvrak(null);
        }

        
        
        
        stajyer.setHesaplananIsGunu(rs.getLong("HESAPLANAN_IS_GUNU")); 
        

        return stajyer;
    }

    public boolean addStajyer(Stajyer stajyer) {
        String sql = "INSERT INTO STAJYERLER (STAJYER_ID, TC_KIMLIK, AD_SOYAD, ADRES, TELEFON_NO, IBAN_NO, DOGUM_TARIHI, " +
                     "OKUL_ID, REFERANS_ID, GIRIS_EVRAK_ID, CIKIS_EVRAK_ID, BOLUM, SINIF, " +
                     "BASLANGIC_TARIHI, BITIS_TARIHI, HESAPLANAN_IS_GUNU) " + 
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; 

        try (Connection conn = DatabaseConnection.getConnection()) {
            
            int newStajyerId;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT NEXT VALUE FOR GEN_STAJYERLER_ID FROM RDB$DATABASE")) {
                if (rs.next()) {
                    newStajyerId = rs.getInt(1);
                } else {
                    throw new SQLException("Generator'dan yeni ID alınamadı.");
                }
            }
            
            
            stajyer.setStajyerId(newStajyerId);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int paramIndex = 1;
                ps.setInt(paramIndex++, stajyer.getStajyerId());
                ps.setString(paramIndex++, stajyer.getTcKimlik());
                ps.setString(paramIndex++, stajyer.getAdSoyad());
                ps.setString(paramIndex++, stajyer.getAdres());
                ps.setString(paramIndex++, stajyer.getTelefonNo());
                ps.setString(paramIndex++, stajyer.getIbanNo());
                ps.setDate(paramIndex++, stajyer.getDogumTarihi() != null ? Date.valueOf(stajyer.getDogumTarihi()) : null);

                
                if (stajyer.getOkul() != null && stajyer.getOkul().getOkulId() > 0) ps.setInt(paramIndex++, stajyer.getOkul().getOkulId()); else ps.setNull(paramIndex++, Types.INTEGER);
                if (stajyer.getReferans() != null && stajyer.getReferans().getReferansId() > 0) ps.setInt(paramIndex++, stajyer.getReferans().getReferansId()); else ps.setNull(paramIndex++, Types.INTEGER);
                if (stajyer.getGirisEvrak() != null && stajyer.getGirisEvrak().getEvrakId() > 0) ps.setInt(paramIndex++, stajyer.getGirisEvrak().getEvrakId()); else ps.setNull(paramIndex++, Types.INTEGER);
                if (stajyer.getCikisEvrak() != null && stajyer.getCikisEvrak().getEvrakId() > 0) ps.setInt(paramIndex++, stajyer.getCikisEvrak().getEvrakId()); else ps.setNull(paramIndex++, Types.INTEGER);

                ps.setString(paramIndex++, stajyer.getBolum());
                ps.setInt(paramIndex++, stajyer.getSinif());
                ps.setDate(paramIndex++, stajyer.getStajBaslangicTarihi() != null ? Date.valueOf(stajyer.getStajBaslangicTarihi()) : null);
                ps.setDate(paramIndex++, stajyer.getStajBitisTarihi() != null ? Date.valueOf(stajyer.getStajBitisTarihi()) : null);
                
                
                ps.setLong(paramIndex++, stajyer.getHesaplananIsGunu());
                

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Stajyer eklenirken hata oluştu.", e);
            return false;
        }
    }

    public boolean updateStajyer(Stajyer stajyer) {
        String sql = "UPDATE STAJYERLER SET TC_KIMLIK = ?, AD_SOYAD = ?, ADRES = ?, TELEFON_NO = ?, IBAN_NO = ?, DOGUM_TARIHI = ?, " +
                     "OKUL_ID = ?, REFERANS_ID = ?, GIRIS_EVRAK_ID = ?, CIKIS_EVRAK_ID = ?, BOLUM = ?, SINIF = ?, " +
                     "BASLANGIC_TARIHI = ?, BITIS_TARIHI = ?, HESAPLANAN_IS_GUNU = ? " + 
                     "WHERE STAJYER_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            ps.setString(paramIndex++, stajyer.getTcKimlik());
            ps.setString(paramIndex++, stajyer.getAdSoyad());
            ps.setString(paramIndex++, stajyer.getAdres());
            ps.setString(paramIndex++, stajyer.getTelefonNo());
            ps.setString(paramIndex++, stajyer.getIbanNo());
            ps.setDate(paramIndex++, stajyer.getDogumTarihi() != null ? Date.valueOf(stajyer.getDogumTarihi()) : null);
            if (stajyer.getOkul() != null && stajyer.getOkul().getOkulId() > 0) ps.setInt(paramIndex++, stajyer.getOkul().getOkulId()); else ps.setNull(paramIndex++, Types.INTEGER);
            if (stajyer.getReferans() != null && stajyer.getReferans().getReferansId() > 0) ps.setInt(paramIndex++, stajyer.getReferans().getReferansId()); else ps.setNull(paramIndex++, Types.INTEGER);
            if (stajyer.getGirisEvrak() != null && stajyer.getGirisEvrak().getEvrakId() > 0) ps.setInt(paramIndex++, stajyer.getGirisEvrak().getEvrakId()); else ps.setNull(paramIndex++, Types.INTEGER);
            if (stajyer.getCikisEvrak() != null && stajyer.getCikisEvrak().getEvrakId() > 0) ps.setInt(paramIndex++, stajyer.getCikisEvrak().getEvrakId()); else ps.setNull(paramIndex++, Types.INTEGER);
            ps.setString(paramIndex++, stajyer.getBolum());
            ps.setInt(paramIndex++, stajyer.getSinif());
            ps.setDate(paramIndex++, stajyer.getStajBaslangicTarihi() != null ? Date.valueOf(stajyer.getStajBaslangicTarihi()) : null);
            ps.setDate(paramIndex++, stajyer.getStajBitisTarihi() != null ? Date.valueOf(stajyer.getStajBitisTarihi()) : null);
            
            
            ps.setLong(paramIndex++, stajyer.getHesaplananIsGunu());
            

            ps.setInt(paramIndex++, stajyer.getStajyerId()); 
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Stajyer güncellenirken hata oluştu.", e);
            return false;
        }
    }

    public Stajyer getStajyerById(int stajyerId) {
        
        
        String sql = "SELECT * FROM STAJYERLER WHERE STAJYER_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stajyerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return populateStajyerFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Stajyer getirilirken hata. ID: " + stajyerId, e);
        }
        return null;
    }

    public List<Stajyer> getAllStajyerler() {
        List<Stajyer> stajyerler = new ArrayList<>();
        
        
        String sql = "SELECT * FROM STAJYERLER";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stajyerler.add(populateStajyerFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Tüm stajyerler getirilirken hata.", e);
        }
        return stajyerler;
    }
    
    public boolean deleteStajyer(int stajyerId) {
        String sql = "DELETE FROM STAJYERLER WHERE STAJYER_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stajyerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Stajyer silinirken hata. ID: " + stajyerId, e);
            return false;
        }
    }
}