package com.sts.stajyertakipsistem.GUI; // veya nerede tanımlıysa

import javax.swing.table.AbstractTableModel;
import java.util.List;
import com.sts.stajyertakipsistem.model.Stajyer; // Stajyer modelini import edin
import java.time.LocalDate;

public class StajyerTableModel extends AbstractTableModel {
    private List<Stajyer> stajyerList;
    private final String[] COLUMN_NAMES = {
        "Ad Soyad", "Adres", "Telefon No", "IBAN No", "Doğum Tarihi",
        "Okul", "Referans", "T.C. Kimlik No", "Başlangıç Tarihi",
        "Bitiş Tarihi", "Bölüm", "Sınıf"
    };

    public StajyerTableModel() {
        // Constructor: Başlangıçta boş liste ile başlar.
        // Veri, StajyerListForm'daki loadStajyerTableData() tarafından set edilir.
        this.stajyerList = new java.util.ArrayList<>();
    }

    public void setStajyerList(List<Stajyer> stajyerList) {
        this.stajyerList = stajyerList;
        fireTableDataChanged(); // Tabloya verilerin değiştiğini bildir
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
            case 4: return stajyer.getDogumTarihi(); // LocalDate olmalı
            case 5: return stajyer.getOkul();
            case 6: return stajyer.getReferans();
            case 7: return stajyer.getTcKimlik();
            case 8: return stajyer.getStajBaslangicTarihi(); // LocalDate olmalı
            case 9: return stajyer.getStajBitisTarihi(); // LocalDate olmalı
            case 10: return stajyer.getBolum();
            case 11: return stajyer.getSinif();
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 4: // Doğum Tarihi
            case 8: // Başlangıç Tarihi
            case 9: // Bitiş Tarihi
                    return LocalDate.class;  
            // Diğer sütunlar genellikle String'dir.
            // Eğer "Sınıf" gibi bir int/Integer ise, o zaman Integer.class döndürmelisiniz.
            default:
                return String.class; // Varsayılan olarak String döndür
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // StajyerListForm'daki tabloda hücreleri düzenlenebilir yapmak istiyorsanız:
        // return true; // Tüm hücreler düzenlenebilir olur

        // Veya sadece belirli sütunları düzenlenebilir yapmak istiyorsanız:
        // if (columnIndex == 0 || columnIndex == 1) { // Örneğin Ad Soyad ve Adres sütunları düzenlenebilir olsun
        //     return true;
        // }
        // return false; // Diğer sütunlar düzenlenemez

        // NOT: Genellikle liste formlarında düzenleme yapılmaz. 
        // Düzenleme için ayrı bir "Stajyer Detay Formu" kullanılır (SpesifikStajyerForm).
        // Bu nedenle, muhtemelen burada false döndürmelisiniz:
        return false; 
    }

    // Eğer isCellEditable() true döndürdüyse, setValueAt() metodunu da implement etmelisiniz:
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (stajyerList == null || rowIndex < 0 || rowIndex >= stajyerList.size()) {
            return;
        }
        Stajyer stajyer = stajyerList.get(rowIndex);
        switch (columnIndex) {
            case 0: stajyer.setAdSoyad((String) aValue); break;
            case 1: stajyer.setAdres((String) aValue); break;
            // Diğer sütunlar için de benzer set metotlarını çağırın
            // case N: stajyer.setXXX((T) aValue); break;
        }
        fireTableCellUpdated(rowIndex, columnIndex); // Sadece ilgili hücrenin güncellendiğini bildir
        // NOT: Değişikliği veritabanına kaydetmek için ayrıca bir mekanizma gereklidir.
        // setValueAt genellikle sadece modeldeki veriyi günceller.
    }
}