package com.sts.stajyertakipsistem.dao;

import com.sts.stajyertakipsistem.model.Stajyer;
import com.sts.stajyertakipsistem.model.Okul;
import com.sts.stajyertakipsistem.model.Referans;
import com.sts.stajyertakipsistem.model.GirisEvrak;
import com.sts.stajyertakipsistem.model.CikisEvrak;
import com.sts.stajyertakipsistem.model.Evrak;

import com.sts.stajyertakipsistem.db.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StajyerDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    private EvrakDAO evrakDAO = new EvrakDAO();
    private OkulDAO okulDAO = new OkulDAO();
    private ReferansDAO referansDAO = new ReferansDAO();

    private Stajyer populateStajyerFromResultSet(ResultSet rs) throws SQLException {
        Stajyer stajyer = new Stajyer();
        stajyer.setStajyerId(rs.getString("STAJYER_ID"));
        stajyer.setTcKimlik(rs.getLong("TC_KIMLIK"));
        stajyer.setAdSoyad(rs.getString("AD_SOYAD"));
        stajyer.setAdres(rs.getString("ADRES"));
        stajyer.setTelefonNo(rs.getString("TELEFON_NO"));
        stajyer.setIbanNo(rs.getString("IBAN_NO"));
       

        Date dogumTarihiSql = rs.getDate("DOGUM_TARIHI");
        stajyer.setDogumTarihi(dogumTarihiSql != null ? dogumTarihiSql.toLocalDate() : null);

        Date stajBaslangicTarihiSql = rs.getDate("STAJ_BASLANGIC_TARIHI");
        stajyer.setStajBaslangicTarihi(stajBaslangicTarihiSql != null ? stajBaslangicTarihiSql.toLocalDate() : null);

        Date stajBitisTarihiSql = rs.getDate("STAJ_BITIS_TARIHI");
        stajyer.setStajBitisTarihi(stajBitisTarihiSql != null ? stajBitisTarihiSql.toLocalDate() : null);

        int okulId = rs.getInt("OKUL_ID");
        if (!rs.wasNull()) {
            stajyer.setOkul(okulDAO.getOkulById(okulId));
        }

        String referansId = rs.getString("REFERANS_ID");
        if (referansId != null) {
            stajyer.setReferans(referansDAO.getReferansById(referansId));
        }

        String girisEvrakId = rs.getString("GIRIS_EVRAK_ID");
        if (girisEvrakId != null) {
            stajyer.setGirisEvrak((GirisEvrak) evrakDAO.getEvrakById(girisEvrakId));
        }

        String cikisEvrakId = rs.getString("CIKIS_EVRAK_ID");
        if (cikisEvrakId != null) {
            stajyer.setCikisEvrak((CikisEvrak) evrakDAO.getEvrakById(cikisEvrakId));
        }

        stajyer.setBolum(rs.getString("BOLUM"));
        stajyer.setSinif(rs.getInt("SINIF"));

        return stajyer;
    }

    public boolean addStajyer(Stajyer stajyer) {
        String sql = "INSERT INTO STAJYERLER (" +
                     "STAJYER_ID, TC_KIMLIK, AD_SOYAD, ADRES, TELEFON_NO, IBAN_NO, DOGUM_TARIHI, " +
                     "OKUL_ID, REFERANS_ID, GIRIS_EVRAK_ID, CIKIS_EVRAK_ID, BOLUM, SINIF, " +
                     "STAJ_BASLANGIC_TARIHI, STAJ_BITIS_TARIHI) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (stajyer.getStajyerId() == null || stajyer.getStajyerId().isEmpty()) {
                stajyer.setStajyerId(UUID.randomUUID().toString());
            }

            String girisEvrakDbId = null;
            if (stajyer.getGirisEvrak() != null) {
                if (stajyer.getGirisEvrak().getEvrakId() == null || stajyer.getGirisEvrak().getEvrakId().isEmpty()) {
                    girisEvrakDbId = evrakDAO.addEvrak(stajyer.getGirisEvrak());
                    stajyer.getGirisEvrak().setEvrakId(girisEvrakDbId);
                } else {
                    evrakDAO.updateEvrak(stajyer.getGirisEvrak());
                    girisEvrakDbId = stajyer.getGirisEvrak().getEvrakId();
                }
            }

            String cikisEvrakDbId = null;
            if (stajyer.getCikisEvrak() != null) {
                if (stajyer.getCikisEvrak().getEvrakId() == null || stajyer.getCikisEvrak().getEvrakId().isEmpty()) {
                    cikisEvrakDbId = evrakDAO.addEvrak(stajyer.getCikisEvrak());
                    stajyer.getCikisEvrak().setEvrakId(cikisEvrakDbId);
                } else {
                    evrakDAO.updateEvrak(stajyer.getCikisEvrak());
                    cikisEvrakDbId = stajyer.getCikisEvrak().getEvrakId();
                }
            }

            int paramIndex = 1;
            ps.setString(paramIndex++, stajyer.getStajyerId());
            ps.setLong(paramIndex++, stajyer.getTcKimlik());
            ps.setString(paramIndex++, stajyer.getAdSoyad());
            ps.setString(paramIndex++, stajyer.getAdres());
            ps.setString(paramIndex++, stajyer.getTelefonNo());
            ps.setString(paramIndex++, stajyer.getIbanNo());
            ps.setDate(paramIndex++, stajyer.getDogumTarihi() != null ? Date.valueOf(stajyer.getDogumTarihi()) : null);

            if (stajyer.getOkul() != null) {
                ps.setInt(paramIndex++, stajyer.getOkul().getOkulId());
            } else {
                ps.setNull(paramIndex++, Types.INTEGER);
            }
            ps.setString(paramIndex++, stajyer.getReferans() != null ? stajyer.getReferans().getReferansId() : null);
            ps.setString(paramIndex++, girisEvrakDbId);
            ps.setString(paramIndex++, cikisEvrakDbId);
            ps.setString(paramIndex++, stajyer.getBolum());
            ps.setInt(paramIndex++, stajyer.getSinif());
            ps.setDate(paramIndex++, stajyer.getStajBaslangicTarihi() != null ? Date.valueOf(stajyer.getStajBaslangicTarihi()) : null);
            ps.setDate(paramIndex++, stajyer.getStajBitisTarihi() != null ? Date.valueOf(stajyer.getStajBitisTarihi()) : null);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStajyer(Stajyer stajyer) {
        String sql = "UPDATE STAJYERLER SET " +
                     "TC_KIMLIK = ?, AD_SOYAD = ?, ADRES = ?, TELEFON_NO = ?, IBAN_NO = ?, DOGUM_TARIHI = ?, " +
                     "OKUL_ID = ?, REFERANS_ID = ?, GIRIS_EVRAK_ID = ?, CIKIS_EVRAK_ID = ?, BOLUM = ?, SINIF = ?, " +
                     "STAJ_BASLANGIC_TARIHI = ?, STAJ_BITIS_TARIHI = ? " +
                     "WHERE STAJYER_ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String girisEvrakDbId = null;
            if (stajyer.getGirisEvrak() != null) {
                if (stajyer.getGirisEvrak().getEvrakId() == null || stajyer.getGirisEvrak().getEvrakId().isEmpty()) {
                    girisEvrakDbId = evrakDAO.addEvrak(stajyer.getGirisEvrak());
                    stajyer.getGirisEvrak().setEvrakId(girisEvrakDbId);
                } else {
                    evrakDAO.updateEvrak(stajyer.getGirisEvrak());
                    girisEvrakDbId = stajyer.getGirisEvrak().getEvrakId();
                }
            }


            String cikisEvrakDbId = null;
            if (stajyer.getCikisEvrak() != null) {
                if (stajyer.getCikisEvrak().getEvrakId() == null || stajyer.getCikisEvrak().getEvrakId().isEmpty()) {
                    cikisEvrakDbId = evrakDAO.addEvrak(stajyer.getCikisEvrak());
                    stajyer.getCikisEvrak().setEvrakId(cikisEvrakDbId);
                } else {
                    evrakDAO.updateEvrak(stajyer.getCikisEvrak());
                    cikisEvrakDbId = stajyer.getCikisEvrak().getEvrakId();
                }
            }

            int paramIndex = 1;
            ps.setLong(paramIndex++, stajyer.getTcKimlik());
            ps.setString(paramIndex++, stajyer.getAdSoyad());
            ps.setString(paramIndex++, stajyer.getAdres());
            ps.setString(paramIndex++, stajyer.getTelefonNo());
            ps.setString(paramIndex++, stajyer.getIbanNo());
            ps.setDate(paramIndex++, stajyer.getDogumTarihi() != null ? Date.valueOf(stajyer.getDogumTarihi()) : null);

            if (stajyer.getOkul() != null) {
                ps.setInt(paramIndex++, stajyer.getOkul().getOkulId());
            } else {
                ps.setNull(paramIndex++, Types.INTEGER);
            }
            ps.setString(paramIndex++, stajyer.getReferans() != null ? stajyer.getReferans().getReferansId() : null);
            ps.setString(paramIndex++, girisEvrakDbId);
            ps.setString(paramIndex++, cikisEvrakDbId);
            ps.setString(paramIndex++, stajyer.getBolum());
            ps.setInt(paramIndex++, stajyer.getSinif());
            ps.setDate(paramIndex++, stajyer.getStajBaslangicTarihi() != null ? Date.valueOf(stajyer.getStajBaslangicTarihi()) : null);
            ps.setDate(paramIndex++, stajyer.getStajBitisTarihi() != null ? Date.valueOf(stajyer.getStajBitisTarihi()) : null);

            ps.setString(paramIndex++, stajyer.getStajyerId());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Stajyer getStajyerById(String stajyerId) {
        Stajyer stajyer = null;
        String sql = "SELECT * FROM STAJYERLER WHERE STAJYER_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stajyerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stajyer = populateStajyerFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stajyer;
    }

    public List<Stajyer> getAllStajyerler() {
        List<Stajyer> stajyerler = new ArrayList<>();
        String sql = "SELECT * FROM STAJYERLER";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                stajyerler.add(populateStajyerFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stajyerler;
    }

    public List<Stajyer> searchStajyerler(String searchText) {
        List<Stajyer> stajyerler = new ArrayList<>();
        String sql = "SELECT * FROM STAJYERLER WHERE AD_SOYAD LIKE ? OR TC_KIMLIK LIKE ? OR BOLUM LIKE ? OR SINIF LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + searchText + "%");
            ps.setString(2, "%" + searchText + "%");
            ps.setString(3, "%" + searchText + "%");
            ps.setString(4, "%" + searchText + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stajyerler.add(populateStajyerFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stajyerler;
    }

    public boolean deleteStajyer(String stajyerId) {
        String sql = "DELETE FROM STAJYERLER WHERE STAJYER_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stajyerId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}