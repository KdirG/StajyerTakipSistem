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
        "İş Günü" 
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
            case 4: 
                return stajyer.getDogumTarihi() != null ? stajyer.getDogumTarihi().format(DISPLAY_DATE_FORMATTER) : "";
            case 5: 
                return stajyer.getOkul() != null ? stajyer.getOkul().getOkulAdi() : ""; 
            case 6: 
                return stajyer.getReferans() != null ? stajyer.getReferans().getAdSoyad() : ""; 
            case 7: return stajyer.getTcKimlik();
            case 8: 
                return stajyer.getStajBaslangicTarihi() != null ? stajyer.getStajBaslangicTarihi().format(DISPLAY_DATE_FORMATTER) : "";
            case 9: 
                return stajyer.getStajBitisTarihi() != null ? stajyer.getStajBitisTarihi().format(DISPLAY_DATE_FORMATTER) : "";
            case 10: return stajyer.getBolum();
            case 11: return stajyer.getSinif();
            case 12: return stajyer.getHesaplananIsGunu(); 
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 4: 
            case 8: 
            case 9: 
                return String.class;
            case 11: return Integer.class; 
            case 12: return Long.class; 
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; 
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        
        if (stajyerList == null || rowIndex < 0 || rowIndex >= stajyerList.size()) {
            return;
        }
        Stajyer stajyer = stajyerList.get(rowIndex);
        switch (columnIndex) {
            case 0: stajyer.setAdSoyad((String) aValue); break;
            case 1: stajyer.setAdres((String) aValue); break;
            case 2: stajyer.setTelefonNo((String) aValue); break;
            case 3: stajyer.setIbanNo((String) aValue); break;
            
            case 7: stajyer.setTcKimlik((String) aValue); break;
            case 10: stajyer.setBolum((String) aValue); break;
            case 11: stajyer.setSinif((Integer) aValue); break;
            
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}