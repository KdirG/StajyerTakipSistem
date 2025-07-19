package com.sts.stajyertakipsistem.model;

public class CikisEvrak extends Evrak {

    // Evrak sınıfının parametre alan constructor'ını çağırır
    public CikisEvrak(String evrakId, String dosyaYolu) {
        super(evrakId, dosyaYolu);
    }

    // Evrak sınıfının parametresiz constructor'ını çağırır
    public CikisEvrak() {
        super();
    }

    @Override
    public String getEvrakTuruAciklama() {
        return "Çıkış Evrağı"; // Tek fark bu açıklama olacak
    }
}