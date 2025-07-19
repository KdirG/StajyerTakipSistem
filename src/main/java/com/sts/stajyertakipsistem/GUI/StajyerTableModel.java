package com.sts.stajyertakipsistem.GUI;

import com.sts.stajyertakipsistem.model.Stajyer;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class StajyerTableModel extends AbstractTableModel {

    private List<Stajyer> stajyerList;
    private final String[] COLUMN_NAMES = {
        "Ad Soyad", "Adres", "Telefon No", "IBAN No", "Doğum Tarihi",
        "Okul", "Referans", "T.C. Kimlik No", "Başlangıç Tarihi", "Bitiş Tarihi",
        "Bölüm", "Sınıf"
    };

    public StajyerTableModel() {
        this.stajyerList = new ArrayList<>();
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
    public String getColumnName(int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

        
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Stajyer stajyer = stajyerList.get(rowIndex);

        switch (columnIndex) {
            case 0: return stajyer.getAdSoyad();
            case 1: return stajyer.getAdres();
            case 2: return stajyer.getTelefonNo();
            case 3: return stajyer.getIbanNo();
            case 4: return stajyer.getDogumTarihi(); 
            case 5: return stajyer.getOkul() != null ? stajyer.getOkul().getOkulAdi() : "";
            case 6: return stajyer.getReferans() != null ? stajyer.getReferans().getAdSoyad() : "";
            case 7: return stajyer.getTcKimlik(); 
            case 8: return stajyer.getStajBaslangicTarihi(); 
            case 9: return stajyer.getStajBitisTarihi(); 
            case 10: return stajyer.getBolum();
            case 11: return stajyer.getSinif();
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}