package com.sts.stajyertakipsistem.model;

public class GirisEvrak extends Evrak {

    // Evrak sınıfının parametre alan constructor'ını çağırır
    public GirisEvrak(String evrakId, String dosyaYolu) {
        super(evrakId, dosyaYolu);
    }

    // Evrak sınıfının parametresiz constructor'ını çağırır
    public GirisEvrak() {
        super();
    }

    @Override
    public String getEvrakTuruAciklama() {
        return "Giriş Evrağı"; // Tek fark bu açıklama olacak
    }
}