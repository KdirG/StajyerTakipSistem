package com.sts.stajyertakipsistem.GUI;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import com.sts.stajyertakipsistem.model.Stajyer;
import com.sts.stajyertakipsistem.model.Okul;
import com.sts.stajyertakipsistem.model.Referans;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StajyerTableModel extends AbstractTableModel {
    private List<Stajyer> stajyerList;
    private final String[] COLUMN_NAMES = {
        "Ad Soyad", "Adres", "Telefon No", "IBAN No", "Doğum Tarihi",
        "Okul", "Referans", "T.C. Kimlik No", "Başlangıç Tarihi",
        "Bitiş Tarihi", "Bölüm", "Sınıf",
        "İş Günü" // <<-- SADECE BU SÜTUN EKLENDİ
    };

    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public StajyerTableModel() {
        this.stajyerList = new java.util.ArrayList<>();
    }

    public void setStajyerList(List<Stajyer> stajyerList) {
        this.stajyerList = stajyerList;
        fireTableDataChanged();
    }

    public Stajyer getStajyerAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < stajyerList.size()) {
            return stajyerList.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return stajyerList.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Stajyer stajyer = stajyerList.get(rowIndex);

        switch (columnIndex) {
            case 0: return stajyer.getAdSoyad();
            case 1: return stajyer.getAdres();
            case 2: return stajyer.getTelefonNo();
            case 3: return stajyer.getIbanNo();
            case 4: // Doğum Tarihi
                return stajyer.getDogumTarihi() != null ? stajyer.getDogumTarihi().format(DISPLAY_DATE_FORMATTER) : "";
            case 5: // Okul objesi
                return stajyer.getOkul() != null ? stajyer.getOkul().getOkulAdi() : ""; // Okul adını göster
            case 6: // Referans objesi
                return stajyer.getReferans() != null ? stajyer.getReferans().getAdSoyad() : ""; // Referans adını göster
            case 7: return stajyer.getTcKimlik();
            case 8: // Staj Başlangıç Tarihi
                return stajyer.getStajBaslangicTarihi() != null ? stajyer.getStajBaslangicTarihi().format(DISPLAY_DATE_FORMATTER) : "";
            case 9: // Staj Bitiş Tarihi
                return stajyer.getStajBitisTarihi() != null ? stajyer.getStajBitisTarihi().format(DISPLAY_DATE_FORMATTER) : "";
            case 10: return stajyer.getBolum();
            case 11: return stajyer.getSinif();
            case 12: return stajyer.getHesaplananIsGunu(); // <<-- YENİ: İş Günü değeri
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 4: // Doğum Tarihi (String olarak gösterildiği için)
            case 8: // Başlangıç Tarihi (String olarak gösterildiği için)
            case 9: // Bitiş Tarihi (String olarak gösterildiği için)
                return String.class;
            case 11: return Integer.class; // Sınıf int olduğu için
            case 12: return Long.class; // <<-- YENİ: Hesaplanan İş Günü long olduğu için
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Hücreler düzenlenemez
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // isCellEditable false olduğu için bu kısım tetiklenmez, ama yine de güncelledim
        if (stajyerList == null || rowIndex < 0 || rowIndex >= stajyerList.size()) {
            return;
        }
        Stajyer stajyer = stajyerList.get(rowIndex);
        switch (columnIndex) {
            case 0: stajyer.setAdSoyad((String) aValue); break;
            case 1: stajyer.setAdres((String) aValue); break;
            case 2: stajyer.setTelefonNo((String) aValue); break;
            case 3: stajyer.setIbanNo((String) aValue); break;
            // Diğer set metodları (tarih, okul, referans gibi karmaşık tipler genellikle burada doğrudan ayarlanmaz)
            case 7: stajyer.setTcKimlik((String) aValue); break;
            case 10: stajyer.setBolum((String) aValue); break;
            case 11: stajyer.setSinif((Integer) aValue); break;
            // case 12: stajyer.setHesaplananIsGunu((Long) aValue); break; // İş gününü elle değiştirmemek daha iyi
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}